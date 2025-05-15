# Analysis Object Model & Workflows

## 1. LearningPath
**Represents** a step-by-step learning roadmap, tailored to a user’s skills and goals.

**Attributes**
- **id: UUID**  
  Unique identifier for this path.
- **title: String**  
  Name of the path (e.g. “AWS Fundamentals”).
- **description: String**  
  A summary of what the path covers.
- **createdAt: Date**, **updatedAt: Date**  
  Timestamps for initial creation and last modification.
- **createdBy: User**  
  The user (or admin) who generated or curated this path.
- **targetSkills: List\<Skill\>**  
  Skills that this path is designed to teach.
- **steps: List\<PathStep\>**  
  Ordered list of the individual learning steps.

**Methods**
- `+addStep(step: PathStep): void`  
  Append a new step to the `steps` list.
- `+removeStep(stepId: UUID): void`  
  Remove the step with the given ID.
- `+generate(userProfile, goal: String): LearningPath`  
  AI-powered constructor: uses the user’s profile and RAG over `ContentModule` embeddings to build a custom sequence of `PathStep`s.

---

## 2. PathStep
**Represents** one ordered unit within a LearningPath, pointing to a specific module.

**Attributes**
- **id: UUID**  
  Unique ID for this step.
- **order: int**  
  Position in the path sequence (1,2,3…).
- **objective: String**  
  Brief description of what to learn in this step.
- **moduleId: UUID**  
  Reference to the `ContentModule` to consume.

**Methods**
- `+markComplete(userId: UUID): void`  
  Mark this step completed on that user’s `ProgressTrack`.
- `+getProgress(userId: UUID): ProgressStatus`  
  Return `NOT_STARTED`/`IN_PROGRESS`/`COMPLETED` for this user-step pair.

---

## 3. ProgressTrack
**Tracks** a user’s completion state for every step in a given path.

**Attributes**
- **id: UUID**
- **userId: UUID**  
  Which user this tracker belongs to.
- **pathId: UUID**  
  Which path is being tracked.
- **stepProgress: Map\<PathStep, ProgressStatus\>**  
  Status of each step.
- **startedAt: Date**, **lastUpdated: Date**  
  When the user first started and last updated progress.

**Methods**
- `+markStepDone(stepId: UUID): void`  
  Mark step as `COMPLETED` and update timestamp.
- `+getCompletionRate(): float`  
  Returns (completed steps) ÷ (total steps).

---

## 4. Skill
**Represents** a technology/topic tag with a proficiency level.

**Attributes**
- **id: UUID**
- **name: String**  
  E.g. “JavaScript,” “Docker.”
- **level: {BEGINNER, INTERMEDIATE, ADVANCED}**

**Methods**
- `+increaseLevel(): void`  
  Advance proficiency one step.
- `+decreaseLevel(): void`  
  Demote one level.

---

## 5. User
**Represents** a learner account.

**Attributes**
- **id: UUID**
- **name: String**
- **email: String**
- **passwordHash: String**
- **avatarUrl: String**
- **registrationDate: Date**
- **skills: List\<Skill\>**
- **enrolledPaths: List\<LearningPath\>**

**Methods**
- `+register(): void`  
  Create account, hash password, set registration date.
- `+login(email, password): AuthToken`  
  Validate credentials, return auth token.
- `+updateProfile(profileDto): void`  
  Change profile fields (name/avatar).
- `+enroll(path: LearningPath): void`  
  Add a path and instantiate its `ProgressTrack`.
- `+addSkill(skill: Skill): void`  
  Append a skill to the user’s profile.

---

## 6. ContentModule
**Represents** a piece of learning content.

**Attributes**
- **id: UUID**
- **title: String**
- **type: {TUTORIAL, ARTICLE, DOCUMENTATION}**
- **url: String**
- **source: String**
- **lengthMinutes: int**
- **tags: List\<String\>**
- **summary: String**
- **teachesSkills: List\<Skill\>**

**Methods**
- `+fetchSummary(): String`  
  Generate or return AI summary.
- `+embedText(): List\<Embedding\>`  
  Produce text-chunk embeddings.
- `+rate(rating: int): void`  
  Record a user rating.

---

## 7. Embedding
**Represents** a vectorized text chunk for semantic search.

**Attributes**
- **id: UUID**
- **vector: float[]**
- **chunkText: String**

**Methods**
- `+computeSimilarity(other: Embedding): float`  
  Cosine similarity helper.

---

## 8. Recommendation
**Suggests** a module to a user with rationale.

**Attributes**
- **id: UUID**
- **userId: UUID**
- **moduleId: UUID**
- **reason: String**
- **recommendedAt: Date**

**Methods**
- `+dismiss(): void`  
  Hide this recommendation from the user.

---

## 9. ContentSource
**Defines** a website or API you crawl for modules.

**Attributes**
- **id: UUID**
- **name: String**
- **baseUrl: String**
- **lastCrawledAt: Date**

---

# Key Workflows

### Scenario A: Onboarding & Profile Setup
1. During registration, the user provides name, email, and password.
2. The system invokes `User.register()`, which hashes the password, records `registrationDate`, and persists the new user.
3. For authentication, the front end calls `User.login(email, password)`, receiving an auth token upon success.
4. To seed initial expertise, the user selects skills (e.g. “JavaScript”, “Git”), and the front end calls `User.addSkill(skill)` for each entry, appending them to the `skills` list.  
   _No AI involvement—this is standard account and profile setup._

---

### Scenario B: Ingesting & Embedding New Content
1. A crawler retrieves a fresh tutorial from a configured `ContentSource`.
2. A new `ContentModule` record is created with metadata (title, URL, etc.).
3. The ingestion pipeline calls `ContentModule.embedText()`, which:
    - Splits the module text into chunks,
    - Generates an `Embedding` vector for each chunk,
    - Persists those embeddings and indexes them in the vector database.
4. Those embeddings become available for downstream semantic search or similarity checks via `Embedding.computeSimilarity(other)`.  
   _No end‐user AI—this prepares the content corpus for RAG._

---

### Scenario C: Generating a Personalized Learning Path
1. Upon request (e.g. “Learn AWS”), the client sends the user’s profile and goal string to `LearningPath.generate(profile, goal)`.
2. The GenAI service performs **retrieval** by querying the embedding index for content chunks most relevant to the goal and existing skills.
3. An LLM is then used for **sequencing**, ordering those chunks into logical learning steps.
4. A new `LearningPath` is instantiated; each ordered item becomes a `PathStep` (with `moduleId`, `objective`, and `order`) via repeated calls to `addStep()`.
5. The completed `LearningPath` (with its `steps` list) is saved and its ID returned.  
   _All AI heavy‐lifting happens inside RAG and LLM sequencing._

---

### Scenario D: Working Through the Path
1. The learner views the ordered `PathStep` list linked to `ContentModule`s.
2. When a step is completed, the application retrieves or creates the corresponding `ProgressTrack` and calls `markStepDone(stepId)`, updating that step’s status.
3. The front end displays overall progress using `getCompletionRate()` (e.g. “2 of 5 steps complete”).  
   _No AI—this is simple state management._

---

### Scenario E: Quick Summaries on Demand
1. The learner requests a summary for a given `ContentModule`.
2. The system invokes `fetchSummary()` on that module.
3. If no valid summary exists, the method sends the full text to an LLM (“Summarize in X bullets”), receives the result, caches it in `module.summary`, and returns it.  
   _On‐demand AI summarization with a single method call._

---

### Scenario F: Fresh Skill‐Gap Recommendations
1. A nightly background job iterates over all users, retrieves each `ProgressTrack` and `LearningPath.targetSkills`.
2. The GenAI service uses the embedding index to find modules not yet encountered that fill remaining skill gaps.
3. An LLM generates a natural‐language `reason` (e.g. “Reinforces EC2 fundamentals”), and `Recommendation` objects are created via `new Recommendation(...)`.
4. On the next login, the client fetches the active recommendations; users may remove any by invoking `dismiss()` on those objects.  
   _Continuous, AI‐driven content suggestions tailored to evolving progress._

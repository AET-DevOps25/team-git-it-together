import * as userService from '@/services/user.service';
import * as courseService from '@/services/course.service';
import { CoursePayload, CourseResponse } from '@/types';

export function setAuthToken(token: string | null) {
  // set it in the user service
  userService.setAuthToken(token);
  // set it in the course service
  courseService.setAuthToken(token);
}

// ------------------------------------------------------------

export async function getAIChatResponse(userId: string, userMessage: string, _userSkills: string[], disableCourseGeneration: boolean = false): Promise<string | CoursePayload | CourseResponse> {
  // Simulate a delay for AI response
  await new Promise((resolve) => setTimeout(resolve, 1000));
  
  let trimmedMessage = userMessage.trim().toLowerCase();
  // Only normalize spaces after the initial slash command
  if (trimmedMessage.startsWith('/')) {
    trimmedMessage = '/' + trimmedMessage.slice(1).replace(/^\s+/, '');
  }
  // Help message
  if (trimmedMessage === '/help') return _displayHelpMessage();
  // Generate a course
  if (trimmedMessage.startsWith('/generate')) {
    if (disableCourseGeneration) {
      return "🚀 **Course Generation Disabled**\n\nPlease visit the **AI Center** page to use the /generate command.\n\nYou can still use:\n• `/explain <subject>` and `/help` commands\n\nOr just ask me anything about your learning journey!";
    }
    const result = await _generateCourse(userId, userMessage, _userSkills);
    // If result is a string that looks like an object, try to parse it
    if (typeof result === 'string') {
      try {
        const parsed = JSON.parse(result);
        if (parsed && typeof parsed === 'object' && (parsed.title || parsed.name)) {
          return parsed;
        }
      } catch { /* ignore JSON parse error, just return as string */ }
    }
    return result;
  }
  // Explain a topic
  if (trimmedMessage.startsWith('/explain')) return _explain(userMessage)
  // Confirm course generation
  if (trimmedMessage === '/confirm') return confirmCourse(userId)
  // Default fallback message
  return _chat(userMessage)
}

async function _chat(prompt: string): Promise<string> {
    // Check if the prompt is empty
    if (!prompt || prompt.trim() === '') {
        return '❌ Please provide a message or question.\n_Example: `/help` or `/explain React hooks`_';
    }
    // Enrich the prompt to keep response of the model short, concise and to the point
    prompt = `Respond to the following message in a natural, friendly, and concise way. 
    - Keep your answer short (no more than 3-4 sentences).
    - Avoid long discussions or lengthy explanations.
    - Be direct and to the point, like a helpful assistant in a chat.
    - If the user asks for a list, use bullet points, but keep it brief.
    
    Message: ${prompt}`;
    
    console.log("AI Chat Prompt:", prompt);
    return courseService.generateResponseFromPrompt(prompt);
}

async function _explain(userMessage: string): Promise<string> {
  console.log("Explaining topic:", userMessage);
  let prompt = userMessage.substring(8).trim();
  if (!prompt) {
    return '❌ Please specify what you want explained.\n_Example: `/explain React hooks`_';
  } else {
    // Improved prompt: bullet points, examples, and easy explanations
    prompt = `Explain the following topic in simple, clear language.\n- Use bullet points for each main idea.\n- Give at least one good example.\n- Focus on the most important points.\n- Make it easy for a beginner to understand.\n\nTopic: ${prompt}`;
    console.log("AI Chat Prompt:", prompt);
    return courseService.generateResponseFromPrompt(prompt);
  }
}

async function _generateCourse(userId: string, userMessage: string, userSkills: string[] ): Promise<CoursePayload | string> {
    const prompt = userMessage.substring(9).trim();
    if (!prompt) {
      return '❌ Please provide a topic or subject for the course.\n_Example: `/generate JavaScript basics`_';
    }
    console.log("Generating course with prompt:", prompt);
    return await courseService.generateCourseForUser(userId, prompt, userSkills);
}

export async function confirmCourse(userId: string): Promise<CourseResponse | string> {
    if (!userId) {
        return '❌ User ID is required to confirm course generation.';
    }
    console.log("Confirming course generation for user:", userId);
    return await courseService.confirmCourseGeneration(userId);
}


function _displayHelpMessage(): string {
  return `🤖 **SkillForge Assistant — Commands & Tips**

          📚 **Course Generation**
          • \`/generate <topic>\` — Instantly create a personalized course for any topic.  
            _Example: \`/generate JavaScript basics\`_

          🔎 **Quick Explanations**
          • \`/explain <subject>\` — Get a simple, clear explanation.  
            _Example: \`/explain React hooks\`_

          💡 **General Help**
          • \`/help\` — Show this help message anytime.

          ✨ **Try these:**
          • \`/generate Python for beginners\`
          • \`/explain machine learning\`
          • Or just type your own question!

          ---
          _Tip: You can mix commands and natural questions—I'm here to help!_ 🤗
          `;
}
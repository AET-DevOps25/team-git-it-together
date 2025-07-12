# Blog Embedder Scheduler


A background service that automatically embeds articles from the freeCodeCamp blog into the vector database every 12 hours.


## Features


- **Automatic Scheduling**: Runs every 12 hours in the background
- **Smart Content Discovery**: Finds up to 5 non-embedded articles (can be older ones)
- **Duplicate Prevention**: Tracks already embedded URLs to avoid re-processing
- **Error Handling**: Graceful error handling with retry logic
- **Non-blocking**: Runs in a separate daemon thread to avoid slowing down the main service
- **API Control**: REST endpoints to control and monitor the scheduler


## How It Works


1. **Smart Article Discovery**: Scans up to 50 recent articles on freeCodeCamp blog
2. **Non-embedded Filtering**: Finds the first 5 articles that haven't been embedded yet
3. **Deep Search**: If latest articles are already embedded, goes deeper into older content
4. **Crawl & Embed**: Uses existing crawler and embedder services to process each article
5. **Store in Vector DB**: Adds the content to Weaviate for semantic search


## API Endpoints


### Get Scheduler Status
```http
GET /api/v1/scheduler/status
```


Response:
```json
{
 "running": true,
 "last_run": "2024-01-15T10:30:00",
 "embedded_count": 25,
 "thread_alive": true
}
```


### Control Scheduler
```http
POST /api/v1/scheduler/control
Content-Type: application/json


{
 "action": "start"  // or "stop"
}
```


### Run Job Immediately
```http
POST /api/v1/scheduler/run-now
```


## Configuration


- **Schedule**: Runs every 12 hours (twice daily)
- **Articles per run**: Up to 5 non-embedded articles
- **Search depth**: Checks up to 50 recent articles to find non-embedded ones
- **Thread type**: Daemon thread (auto-stops when main service stops)


## Monitoring


Check the logs for scheduler activity:
- `"Starting scheduled freeCodeCamp blog embedding job"`
- `"Found X non-embedded articles from freeCodeCamp (checked Y articles)"`
- `"Successfully embedded URL with X chunks"`
- `"No new articles found to embed"`


## Error Handling


- **Network errors**: Retries after 5 minutes
- **Empty content**: Skips and continues with next article
- **Rate limiting**: 2-second delay between articles to be respectful
- **Thread safety**: Runs in isolated daemon thread


## Testing


Run the test script to verify functionality:
```bash
cd genai
python test_scheduler.py
```


## Benefits


- **Efficient**: Only processes articles that haven't been embedded
- **Comprehensive**: Finds older articles if recent ones are already processed
- **Respectful**: Minimal server load with 12-hour intervals
- **Reliable**: Continues working even if individual articles fail


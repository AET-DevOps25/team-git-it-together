from pydantic import BaseModel
from typing import Optional
from datetime import datetime


class SchedulerStatus(BaseModel):
   running: bool
   last_run: Optional[str] = None
   embedded_count: int
   thread_alive: bool


class SchedulerControl(BaseModel):
   action: str  # "start" or "stop"

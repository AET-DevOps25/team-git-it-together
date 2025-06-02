export interface LessonContent {
  type: LessonContentType;
  content: string;
}

export type LessonContentType = 'TEXT' | 'HTML' | 'URL' | 'VIDEO' | 'AUDIO' | 'IMAGE';
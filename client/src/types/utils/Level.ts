export type Level = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';

export const LEVEL_TO_PERCENT: Record<Level, number> = {
  BEGINNER: 25,
  INTERMEDIATE: 50,
  ADVANCED: 75,
  EXPERT: 100,
};
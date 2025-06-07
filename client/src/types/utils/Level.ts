export enum Level {
  BEGINNER = 'BEGINNER',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED',
  EXPERT = 'EXPERT',
}

export const LEVEL_TO_PERCENT: Record<Level, number> = {
  [Level.BEGINNER]: 25,
  [Level.INTERMEDIATE]: 50,
  [Level.ADVANCED]: 75,
  [Level.EXPERT]: 100,
};

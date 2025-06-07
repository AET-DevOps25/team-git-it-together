// Enum for your code
export enum Language {
  ENGLISH = 'ENGLISH',
  SPANISH = 'SPANISH',
  FRENCH = 'FRENCH',
  GERMAN = 'GERMAN',
  CHINESE = 'CHINESE',
  JAPANESE = 'JAPANESE',
  KOREAN = 'KOREAN',
  RUSSIAN = 'RUSSIAN',
  PORTUGUESE = 'PORTUGUESE',
  ARABIC = 'ARABIC',
  ITALIAN = 'ITALIAN',
}

// Map with ISO 639-1 codes and display names
export const LANGUAGE_META: Record<
  Language,
  {
    iso639_1: string;
    displayName: string;
    nativeName: string;
    locale: string;
    flag?: string;
  }
> = {
  [Language.ENGLISH]: {
    iso639_1: 'en',
    displayName: 'English',
    nativeName: 'English',
    locale: 'en-US',
    flag: '🇬🇧',
  },
  [Language.SPANISH]: {
    iso639_1: 'es',
    displayName: 'Spanish',
    nativeName: 'Español',
    locale: 'es-ES',
    flag: '🇪🇸',
  },
  [Language.FRENCH]: {
    iso639_1: 'fr',
    displayName: 'French',
    nativeName: 'Français',
    locale: 'fr-FR',
    flag: '🇫🇷',
  },
  [Language.GERMAN]: {
    iso639_1: 'de',
    displayName: 'German',
    nativeName: 'Deutsch',
    locale: 'de-DE',
    flag: '🇩🇪',
  },
  [Language.CHINESE]: {
    iso639_1: 'zh',
    displayName: 'Chinese',
    nativeName: '中文',
    locale: 'zh-CN',
    flag: '🇨🇳',
  },
  [Language.JAPANESE]: {
    iso639_1: 'ja',
    displayName: 'Japanese',
    nativeName: '日本語',
    locale: 'ja-JP',
    flag: '🇯🇵',
  },
  [Language.KOREAN]: {
    iso639_1: 'ko',
    displayName: 'Korean',
    nativeName: '한국어',
    locale: 'ko-KR',
    flag: '🇰🇷',
  },
  [Language.RUSSIAN]: {
    iso639_1: 'ru',
    displayName: 'Russian',
    nativeName: 'Русский',
    locale: 'ru-RU',
    flag: '🇷🇺',
  },
  [Language.PORTUGUESE]: {
    iso639_1: 'pt',
    displayName: 'Portuguese',
    nativeName: 'Português',
    locale: 'pt-PT',
    flag: '🇵🇹',
  },
  [Language.ARABIC]: {
    iso639_1: 'ar',
    displayName: 'Arabic',
    nativeName: 'العربية',
    locale: 'ar-SA',
    flag: '🇸🇦',
  },
  [Language.ITALIAN]: {
    iso639_1: 'it',
    displayName: 'Italian',
    nativeName: 'Italiano',
    locale: 'it-IT',
    flag: '🇮🇹',
  },
};

// Enum for your code
export enum Language {
  EN = 'EN',
  ES = 'ES',
  FR = 'FR',
  DE = 'DE',
  AR = 'AR',
  ZH = 'ZH',
  JA = 'JA',
  KO = 'KO',
  RU = 'RU',
  PT = 'PT',
  IT = 'IT',
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
  [Language.EN]: {
    iso639_1: 'en',
    displayName: 'English',
    nativeName: 'English',
    locale: 'en-US',
    flag: '🇬🇧',
  },
  [Language.ES]: {
    iso639_1: 'es',
    displayName: 'Spanish',
    nativeName: 'Español',
    locale: 'es-ES',
    flag: '🇪🇸',
  },
  [Language.FR]: {
    iso639_1: 'fr',
    displayName: 'French',
    nativeName: 'Français',
    locale: 'fr-FR',
    flag: '🇫🇷',
  },
  [Language.DE]: {
    iso639_1: 'de',
    displayName: 'German',
    nativeName: 'Deutsch',
    locale: 'de-DE',
    flag: '🇩🇪',
  },
  [Language.AR]: {
    iso639_1: 'ar',
    displayName: 'Arabic',
    nativeName: 'العربية',
    locale: 'ar-SA',
    flag: '🇸🇦',
  },
  [Language.ZH]: {
    iso639_1: 'zh',
    displayName: 'Chinese',
    nativeName: '中文',
    locale: 'zh-CN',
    flag: '🇨🇳',
  },
  [Language.JA]: {
    iso639_1: 'ja',
    displayName: 'Japanese',
    nativeName: '日本語',
    locale: 'ja-JP',
    flag: '🇯🇵',
  },
  [Language.KO]: {
    iso639_1: 'ko',
    displayName: 'Korean',
    nativeName: '한국어',
    locale: 'ko-KR',
    flag: '🇰🇷',
  },
  [Language.RU]: {
    iso639_1: 'ru',
    displayName: 'Russian',
    nativeName: 'Русский',
    locale: 'ru-RU',
    flag: '🇷🇺',
  },
  [Language.PT]: {
    iso639_1: 'pt',
    displayName: 'Portuguese',
    nativeName: 'Português',
    locale: 'pt-PT',
    flag: '🇵🇹',
  },
  [Language.IT]: {
    iso639_1: 'it',
    displayName: 'Italian',
    nativeName: 'Italiano',
    locale: 'it-IT',
    flag: '🇮🇹',
  },
};

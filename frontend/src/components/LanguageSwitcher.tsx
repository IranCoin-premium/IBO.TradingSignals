import React, { useState, useEffect } from 'react';
import './LanguageSwitcher.css';

export interface LanguageOption {
  code: 'fa' | 'en' | 'ar' | 'hi' | 'tr' | 'ru';
  nameNative: string;
  nameEnglish: string;
  direction: 'rtl' | 'ltr';
  flagSvg: React.ReactNode;
}

export const SUPPORTED_LANGUAGES: LanguageOption[] = [
  {
    code: 'fa',
    nameNative: 'فارسی',
    nameEnglish: 'Persian',
    direction: 'rtl',
    flagSvg: (
      <svg viewBox="0 0 640 480" className="lang-flag-svg" aria-hidden="true">
        <path fill="#239f40" d="M0 0h640v160H0z"/>
        <path fill="#fff" d="M0 160h640v160H0z"/>
        <path fill="#da0000" d="M0 320h640v160H0z"/>
        <circle cx="320" cy="240" r="28" fill="#da0000" opacity="0.85"/>
      </svg>
    )
  },
  {
    code: 'en',
    nameNative: 'English',
    nameEnglish: 'English',
    direction: 'ltr',
    flagSvg: (
      <svg viewBox="0 0 640 480" className="lang-flag-svg" aria-hidden="true">
        <path fill="#012169" d="M0 0h640v480H0z"/>
        <path fill="#fff" d="M75 0l245 180L565 0h75v55L400 205l240 175v100h-75L320 300 75 480H0v-55l240-185L0 55V0z"/>
        <path fill="#c8102e" d="M425 285l215 155v40L390 285zM240 205L0 30v40l190 135zM640 30L400 205v-40L590 30zM0 450l240-175v40L50 450z"/>
        <path fill="#fff" d="M260 0h120v480H260zM0 180h640v120H0z"/>
        <path fill="#c8102e" d="M280 0h80v480H280zM0 200h640v80H0z"/>
      </svg>
    )
  },
  {
    code: 'ar',
    nameNative: 'العربية',
    nameEnglish: 'Arabic',
    direction: 'rtl',
    flagSvg: (
      <svg viewBox="0 0 640 480" className="lang-flag-svg" aria-hidden="true">
        <path fill="#007a3d" d="M0 0h640v480H0z"/>
        <path fill="#fff" d="M200 240h240v12H200z" opacity="0.9"/>
        <circle cx="320" cy="210" r="22" fill="#fff" opacity="0.9"/>
      </svg>
    )
  },
  {
    code: 'hi',
    nameNative: 'हिन्दी',
    nameEnglish: 'Hindi',
    direction: 'ltr',
    flagSvg: (
      <svg viewBox="0 0 640 480" className="lang-flag-svg" aria-hidden="true">
        <path fill="#f4c430" d="M0 0h640v160H0z"/>
        <path fill="#fff" d="M0 160h640v160H0z"/>
        <path fill="#006400" d="M0 320h640v160H0z"/>
        <circle cx="320" cy="240" r="28" fill="#000080"/>
        <circle cx="320" cy="240" r="14" fill="#fff"/>
        <circle cx="320" cy="240" r="6" fill="#000080"/>
      </svg>
    )
  },
  {
    code: 'tr',
    nameNative: 'Türkçe',
    nameEnglish: 'Turkish',
    direction: 'ltr',
    flagSvg: (
      <svg viewBox="0 0 640 480" className="lang-flag-svg" aria-hidden="true">
        <path fill="#e30a17" d="M0 0h640v480H0z"/>
        <circle cx="280" cy="240" r="100" fill="#fff"/>
        <circle cx="305" cy="240" r="80" fill="#e30a17"/>
        <polygon points="380,240 340,252 355,212 355,268 340,228" fill="#fff"/>
      </svg>
    )
  },
  {
    code: 'ru',
    nameNative: 'Русский',
    nameEnglish: 'Russian',
    direction: 'ltr',
    flagSvg: (
      <svg viewBox="0 0 640 480" className="lang-flag-svg" aria-hidden="true">
        <path fill="#fff" d="M0 0h640v160H0z"/>
        <path fill="#0039a6" d="M0 160h640v160H0z"/>
        <path fill="#d52b1e" d="M0 320h640v160H0z"/>
      </svg>
    )
  }
];

export interface LanguageSwitcherProps {
  currentLanguage?: 'fa' | 'en' | 'ar' | 'hi' | 'tr' | 'ru';
  onLanguageChange?: (newLang: LanguageOption) => void;
  className?: string;
}

export const LanguageSwitcher: React.FC<LanguageSwitcherProps> = ({
  currentLanguage = 'fa',
  onLanguageChange,
  className = ''
}) => {
  const [activeCode, setActiveCode] = useState<string>(currentLanguage);

  useEffect(() => {
    setActiveCode(currentLanguage);
  }, [currentLanguage]);

  const handleSelectLanguage = (lang: LanguageOption) => {
    setActiveCode(lang.code);
    // Update HTML root attributes for complete RTL/LTR directional support
    document.documentElement.lang = lang.code;
    document.documentElement.dir = lang.direction;

    // Optional: persist in local storage
    try {
      localStorage.setItem('ibo_preferred_language', lang.code);
    } catch {
      // safe fallback
    }

    if (onLanguageChange) {
      onLanguageChange(lang);
    }
  };

  return (
    <nav
      className={`ibo-lang-switcher-container ${className}`}
      aria-label="Language Selector"
      role="navigation"
    >
      <div className="ibo-lang-circles-stack" role="group" aria-label="Available Languages">
        {SUPPORTED_LANGUAGES.map((lang, index) => {
          const isActive = lang.code === activeCode;
          return (
            <button
              key={lang.code}
              type="button"
              className={`ibo-lang-circle ${isActive ? 'is-active' : 'is-inactive'}`}
              onClick={() => handleSelectLanguage(lang)}
              title={`${lang.nameNative} (${lang.nameEnglish})`}
              aria-label={`Switch language to ${lang.nameEnglish} - ${lang.nameNative}`}
              aria-pressed={isActive}
              style={{
                // Compute subtle stacking order: Active circle is always on top (z-index 10)
                zIndex: isActive ? 10 : index + 1,
              }}
            >
              <div className="ibo-lang-flag-wrapper">
                {lang.flagSvg}
              </div>
              <span className="ibo-lang-code-tag">{lang.code.toUpperCase()}</span>
            </button>
          );
        })}
      </div>
    </nav>
  );
};

export default LanguageSwitcher;

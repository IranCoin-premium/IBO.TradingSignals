import { Request, Response } from 'express';

export interface HreflangLink {
  lang: string;
  url: string;
}

export interface SeoPageMetadata {
  lang: string;
  title: string;
  description: string;
  canonicalUrl: string;
  hreflangs: HreflangLink[];
  schemaOrgJsonLd: Record<string, any>;
  riskDisclosure: string;
}

const SUPPORTED_LANGUAGES = ['fa', 'en', 'ar', 'hi', 'tr', 'ru'];
const BASE_DOMAIN = 'https://yourdomain.com';

const MANDATORY_RISK_DISCLOSURES: Record<string, string> = {
  fa: 'این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.',
  en: 'These signals do not constitute financial advice; binary options trading carries a high risk of capital loss.',
  ar: 'هذه الإشارات ليست نصيحة مالية؛ ينطوي تداول الخيارات الثنائية على مخاطر عالية لفقدان رأس المال.',
  hi: 'ये संकेत वित्तीय सलाह नहीं हैं; बाइनरी ऑप्शंस ट्रेडिंग में पूंजी हानि का उच्च जोखिम होता है।',
  tr: 'Bu sinyaller finansal tavsiye niteliğinde değildir; ikili opsiyon işlemleri yüksek sermaye kaybı riski taşır.',
  ru: 'Эти сигналы не являются финансовой рекомендацией; торговля бинарными опционами сопряжена с высоким риском потери капитала.'
};

const LOCALIZED_TITLES: Record<string, { title: string; description: string }> = {
  fa: {
    title: 'سیگنال‌های معاملاتی باینری آپشن IBO | تحلیلگر انسانی + تایید هوش مصنوعی',
    description: 'دریافت سیگنال‌های تحلیلی باینری آپشن با تایید الگوریتم‌های هوش مصنوعی و تحلیلگران بازار. این سیگنال‌ها توصیه مالی نیستند.'
  },
  en: {
    title: 'IBO Binary Option Trading Signals | Human Analyst + AI Verification',
    description: 'Algorithmic and human-verified binary options trading signals. These signals do not constitute financial advice.'
  },
  ar: {
    title: 'إشارات تداول الخيارات الثنائية IBO | محلل بشري + تأكيد الذكاء الاصطناعي',
    description: 'احصل على إشارات تداول الخيارات الثنائية بالتحليل البشري والذكاء الاصطناعي. هذه الإشارات ليست نصيحة مالية.'
  },
  hi: {
    title: 'IBO बाइनरी ऑप्शंस ट्रेडिंग सिग्नल | मानव विश्लेषक + एआई सत्यापन',
    description: 'मानव विश्लेषकों और एआई द्वारा सत्यापित बाइनरी ऑप्शंस ट्रेडिंग सिग्नल। ये संकेत वित्तीय सलाह नहीं हैं।'
  },
  tr: {
    title: 'IBO İkili Opsiyon İşlem Sinyalleri | İnsan Analist + Yapay Zeka Onayı',
    description: 'İnsan analist ve yapay zeka doğrulamalı ikili opsiyon işlem sinyalleri. Bu sinyaller finansal tavsiye değildir.'
  },
  ru: {
    title: 'Торговые сигналы бинарных опционов IBO | Человек-аналитик + ИИ верификация',
    description: 'Сигналы для торговли бинарными опционами с подтверждением ИИ и аналитиками. Не является финансовой рекомендацией.'
  }
};

/**
 * Generates Schema.org JSON-LD for Homepage (Organization & Financial Product)
 */
export function generateHomepageSchema(lang: string): Record<string, any> {
  const currentLang = SUPPORTED_LANGUAGES.includes(lang) ? lang : 'en';
  return {
    '@context': 'https://schema.org',
    '@graph': [
      {
        '@type': 'Organization',
        '@id': `${BASE_DOMAIN}/#organization`,
        name: 'IBO Trading Signals',
        url: BASE_DOMAIN,
        logo: `${BASE_DOMAIN}/assets/branding/ibo_logo_nobg.png`,
        description: LOCALIZED_TITLES[currentLang].description,
        sameAs: [
          'https://t.me/ibotrading',
          'https://twitter.com/ibosignals'
        ]
      },
      {
        '@type': 'Product',
        '@id': `${BASE_DOMAIN}/${currentLang}/#subscription-product`,
        name: 'IBO Binary Option Signals Subscription',
        description: LOCALIZED_TITLES[currentLang].description,
        brand: {
          '@id': `${BASE_DOMAIN}/#organization`
        },
        offers: {
          '@type': 'AggregateOffer',
          priceCurrency: 'USDT',
          lowPrice: '29',
          highPrice: '199',
          offerCount: '3',
          offers: [
            {
              '@type': 'Offer',
              name: '1 Month Standard Plan',
              price: '29',
              priceCurrency: 'USDT',
              availability: 'https://schema.org/InStock'
            },
            {
              '@type': 'Offer',
              name: '3 Months VIP Plan',
              price: '79',
              priceCurrency: 'USDT',
              availability: 'https://schema.org/InStock'
            }
          ]
        },
        disambiguatingDescription: MANDATORY_RISK_DISCLOSURES[currentLang]
      }
    ]
  };
}

/**
 * Generates Schema.org JSON-LD for Financial News / Economic Journal Articles
 */
export function generateNewsArticleSchema(params: {
  headline: string;
  description: string;
  articleUrl: string;
  imageUrl: string;
  datePublished: string;
  dateModified?: string;
  newsSourceName: string;
  lang: string;
}): Record<string, any> {
  const currentLang = SUPPORTED_LANGUAGES.includes(params.lang) ? params.lang : 'en';
  return {
    '@context': 'https://schema.org',
    '@type': 'NewsArticle',
    mainEntityOfPage: {
      '@type': 'WebPage',
      '@id': params.articleUrl
    },
    headline: params.headline,
    description: params.description,
    image: [params.imageUrl],
    datePublished: params.datePublished,
    dateModified: params.dateModified || params.datePublished,
    author: {
      '@type': 'Organization',
      name: 'IBO Binary Option Trading Signals',
      url: BASE_DOMAIN
    },
    publisher: {
      '@type': 'Organization',
      name: 'IBO Binary Option Trading Signals — yourdomain.com',
      logo: {
        '@type': 'ImageObject',
        url: `${BASE_DOMAIN}/assets/branding/ibo_logo_nobg.png`
      }
    },
    isAccessibleForFree: true,
    inLanguage: currentLang,
    sourceOrganization: {
      '@type': 'Organization',
      name: params.newsSourceName
    },
    disclaimer: MANDATORY_RISK_DISCLOSURES[currentLang]
  };
}

/**
 * Generates dynamic multilingual sitemap XML
 */
export function generateMultilingualSitemapXml(): string {
  const staticRoutes = ['', 'plans', 'news', 'faq', 'risk-warning'];
  const today = new Date().toISOString().slice(0, 10);

  let xml = '<?xml version="1.0" encoding="UTF-8"?>\n';
  xml += '<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:xhtml="http://www.w3.org/1999/xhtml">\n';

  for (const route of staticRoutes) {
    for (const lang of SUPPORTED_LANGUAGES) {
      const pagePath = route ? `/${lang}/${route}` : `/${lang}/`;
      xml += '  <url>\n';
      xml += `    <loc>${BASE_DOMAIN}${pagePath}</loc>\n`;
      xml += `    <lastmod>${today}</lastmod>\n`;
      xml += '    <changefreq>daily</changefreq>\n';
      xml += `    <priority>${route === '' ? '1.0' : '0.8'}</priority>\n`;

      // Hreflang alternates for all supported languages + x-default
      for (const altLang of SUPPORTED_LANGUAGES) {
        const altPath = route ? `/${altLang}/${route}` : `/${altLang}/`;
        xml += `    <xhtml:link rel="alternate" hreflang="${altLang}" href="${BASE_DOMAIN}${altPath}"/>\n`;
      }
      xml += `    <xhtml:link rel="alternate" hreflang="x-default" href="${BASE_DOMAIN}${route ? `/${route}` : '/'}" />\n`;
      xml += '  </url>\n';
    }
  }

  xml += '</urlset>';
  return xml;
}

/**
 * Controller: GET /sitemap.xml
 */
export const getSitemapXml = (req: Request, res: Response) => {
  const sitemap = generateMultilingualSitemapXml();
  res.header('Content-Type', 'application/xml');
  res.status(200).send(sitemap);
};

/**
 * Controller: GET /api/v1/seo/metadata/:lang
 */
export const getSeoMetadata = (req: Request, res: Response) => {
  const lang = req.params.lang?.toLowerCase() || 'fa';
  const effectiveLang = SUPPORTED_LANGUAGES.includes(lang) ? lang : 'fa';
  const pageRoute = (req.query.route as string) || '';

  const hreflangs: HreflangLink[] = SUPPORTED_LANGUAGES.map((l) => ({
    lang: l,
    url: `${BASE_DOMAIN}/${l}/${pageRoute}`.replace(/\/+$/, '')
  }));
  hreflangs.push({
    lang: 'x-default',
    url: `${BASE_DOMAIN}/${pageRoute}`.replace(/\/+$/, '')
  });

  const pageInfo = LOCALIZED_TITLES[effectiveLang];
  const schema = generateHomepageSchema(effectiveLang);

  res.status(200).json({
    status: 'success',
    data: {
      lang: effectiveLang,
      title: pageInfo.title,
      description: pageInfo.description,
      canonicalUrl: `${BASE_DOMAIN}/${effectiveLang}/${pageRoute}`.replace(/\/+$/, ''),
      hreflangs,
      schemaOrgJsonLd: schema,
      riskDisclosure: MANDATORY_RISK_DISCLOSURES[effectiveLang]
    }
  });
};

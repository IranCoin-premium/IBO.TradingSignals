import { Router } from 'express';
import { getSeoMetadata, getSitemapXml } from './seo.service';

const router = Router();

// Public SEO Endpoints
router.get('/metadata/:lang', getSeoMetadata);
router.get('/sitemap', getSitemapXml);

export default router;

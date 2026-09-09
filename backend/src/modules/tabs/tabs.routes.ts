import { Router, Response, NextFunction } from 'express';
import { ReferralRepository } from '../referral/referral.repository';

// Platform Tab Registry manifest — consumed by AI Studio client to bind new tabs
// (referral & affiliate, sales partnership) to backend-ready endpoints.
const router = Router();

router.get('/manifest', async (_req: any, res: Response, next: NextFunction) => {
  try {
    const tabs = await ReferralRepository.listTabs();
    res.status(200).json({
      status: 'success',
      data: {
        readiness: 'backend_ready',
        integration_target: 'ai_studio_client_tabs',
        tabs: tabs.map((t: any) => ({
          tab_key: t.tab_key,
          title_i18n_key: t.title_i18n_key,
          route: t.route,
          icon: t.icon,
          audience: t.audience,
          status: t.status,
          feature_flags: t.feature_flags,
        })),
      },
    });
  } catch (error) { next(error); }
});

export default router;

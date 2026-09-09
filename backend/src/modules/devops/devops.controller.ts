import { Request, Response } from 'express';
import { DevopsRepository } from './devops.repository';
import { logger } from '../../utils/logger';
import crypto from 'crypto';

export class DevopsController {
  static async recordRelease(req: Request, res: Response): Promise<void> {
    try {
      const { version, environment, commit_sha, status, metadata } = req.body;
      if (!version || !environment || !commit_sha) {
        res.status(400).json({
          errorCode: 'VALIDATION_ERROR',
          message: 'نسخه، محیط انتشار و هش کامیت (commit_sha) الزامی است.'
        });
        return;
      }

      const releaseId = `rel_${crypto.randomBytes(8).toString('hex')}`;
      const user = (req as any).user;
      const deployedBy = user ? (user.userId || user.id) : 'system-ci';

      const release = await DevopsRepository.recordRelease(
        releaseId,
        version,
        environment,
        commit_sha,
        status || 'SUCCESS',
        deployedBy,
        metadata || {}
      );

      logger.info(`[DEVOPS RELEASE] Recorded release ${releaseId} for version ${version} in ${environment}`);

      res.status(201).json({
        success: true,
        release,
        message: 'Release audit recorded successfully with observability tags.'
      });
    } catch (error: any) {
      logger.error('Error recording release:', error);
      res.status(500).json({
        errorCode: 'INTERNAL_SERVER_ERROR',
        message: 'خطا در ثبت اطلاعات انتشار.'
      });
    }
  }

  static async getReleases(req: Request, res: Response): Promise<void> {
    try {
      const releases = await DevopsRepository.getReleases();
      res.status(200).json({
        success: true,
        releases
      });
    } catch (error: any) {
      logger.error('Error fetching releases:', error);
      res.status(500).json({
        errorCode: 'INTERNAL_SERVER_ERROR',
        message: 'خطا در دریافت تاریخچه انتشار.'
      });
    }
  }

  static async getHealthMetrics(req: Request, res: Response): Promise<void> {
    try {
      res.status(200).json({
        success: true,
        status: 'HEALTHY',
        uptime: process.uptime(),
        memoryUsage: process.memoryUsage(),
        timestamp: new Date().toISOString()
      });
    } catch (error: any) {
      logger.error('Error fetching health metrics:', error);
      res.status(500).json({
        errorCode: 'INTERNAL_SERVER_ERROR',
        message: 'خطا در دریافت وضعیت سلامت سیستم.'
      });
    }
  }
}

import { Request, Response } from 'express';
import { AutonomousAgentEngine, AgentName } from './autonomous.service';
import { logger } from '../../utils/logger';

export const getAgentAutonomyDetails = (req: Request, res: Response) => {
  const agentName = req.params.agent_name as AgentName;
  const validAgents: AgentName[] = ['UIUXAgent', 'TranslatorAgent', 'ImageVideoAgent', 'SupportAgent'];

  if (!validAgents.includes(agentName)) {
    return res.status(404).json({
      status: 'error',
      message: `عامل هوش مصنوعی با نام ${agentName} یافت نشد.`
    });
  }

  const details = AutonomousAgentEngine.getAgentDetails(agentName);
  res.status(200).json({
    status: 'success',
    data: details
  });
};

export const updateAgentAutonomySettings = (req: Request, res: Response) => {
  const agentName = req.params.agent_name as AgentName;
  const adminEmail = (req as any).user?.email || 'admin@ibo.ir';
  const { autonomy_enabled, max_retry, circuit_breaker_threshold, conservativeness_level, circuit_broken } = req.body;

  const updated = AutonomousAgentEngine.updateAutonomySettings(
    agentName,
    {
      autonomy_enabled,
      max_retry,
      circuit_breaker_threshold,
      conservativeness_level,
      circuit_broken
    },
    adminEmail
  );

  if (!updated) {
    return res.status(404).json({
      status: 'error',
      message: 'ایجنت مورد نظر یافت نشد.'
    });
  }

  res.status(200).json({
    status: 'success',
    message: 'تنظیمات استقلال و رفتاری ایجنت با موفقیت به‌روزرسانی شد.',
    data: updated
  });
};

export const rollbackAgentInstruction = (req: Request, res: Response) => {
  const agentName = req.params.agent_name as AgentName;
  const versionNumber = parseInt(req.params.version_number, 10);
  const adminEmail = (req as any).user?.email || 'admin@ibo.ir';

  const success = AutonomousAgentEngine.rollbackInstructionToVersion(agentName, versionNumber, adminEmail);
  if (!success) {
    return res.status(400).json({
      status: 'error',
      message: 'نسخه درخواستی برای بازگردانی معتبر نیست یا یافت نشد.'
    });
  }

  res.status(200).json({
    status: 'success',
    message: `دستورالعمل سیستم ایجنت ${agentName} با موفقیت به نسخه ${versionNumber} بازگردانی شد.`
  });
};

export const triggerSelfTuningRun = async (req: Request, res: Response) => {
  const agentName = req.params.agent_name as AgentName;
  const { proposed_instruction, change_summary } = req.body;

  if (!proposed_instruction) {
    return res.status(400).json({
      status: 'error',
      message: 'متن پیش‌نویس دستورالعمل الزامی است.'
    });
  }

  const result = await AutonomousAgentEngine.executeSelfTuning(
    agentName,
    proposed_instruction,
    change_summary || 'Manual triggered self-tuning draft evaluation'
  );

  if (!result.success) {
    return res.status(422).json({
      status: 'failed',
      message: result.reason,
      data: result.testResult
    });
  }

  res.status(200).json({
    status: 'success',
    message: `دستورالعمل پیشنهادی با موفقیت تمام ۱۰ مرحله تست محیط Staging را پشت سر گذاشت و به نسخه ${result.activeVersion} در پروداکشن ارتقا یافت.`,
    data: result
  });
};

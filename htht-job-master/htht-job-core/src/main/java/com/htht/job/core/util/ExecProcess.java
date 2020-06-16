package com.htht.job.core.util;/**
 * Created by zzj on 2018/4/17.
 */

import com.htht.job.core.log.XxlJobLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: htht-job
 * @description: 调用cmd
 * @author: zzj
 * @create: 2018-04-17 14:38
 **/


public class ExecProcess {
    /**
     * 执行子任务的进程
     */
    private Process p = null;

    public static void main(String[] args) {
        try {
            ExecProcess exec = new ExecProcess();
            if (args.length == 0) {
                throw new RuntimeException("无效的参数");
            }
            StringBuilder commond = new StringBuilder();
            // commond.append(StringUtil.buildCmdArgs(args));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("ExecCommond start Time:" + format.format(new Date()));
            CmdMessage cmdMsg = exec.execCmd(commond.toString(), null);
            System.out.println("ExecCommond end Time:" + format.format(new Date()));
            if (!"".equals(cmdMsg.getError())) {
                System.err.println("[error]:" + cmdMsg.getError());
            }
            if (!"".equals(cmdMsg.getOutput())) {
                System.out.println("[output]:" + cmdMsg.getOutput());
            } else {
                System.out.println("[output]:算法执行完成");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 执行cmd 程序，错误流消息和控制台消息不进行输出
     *
     * @param cmd
     * @return
     */
    public int exec(String cmd, String outputLog) {
        return exec(cmd, false, outputLog);
    }

    /**
     * 调用子进程开始执行cmd，控制台消息是否重定向回去
     *
     * @param cmd
     * @param redirct
     * @return
     */
    public int exec(String cmd, final boolean redirct, String outputLog) {
        CmdMessage cmdmsg = execCmd(cmd, redirct, null);
        return cmdmsg.getCode();
    }

    /**
     * 执行cmd命令，是否重定向控制台消息
     *
     * @param cmd     cmd 命令
     * @param redirct
     * @return
     */
    public CmdMessage execCmd(String cmd, final boolean redirct, String outputLog) {
        final CmdMessage cmdMsg = new CmdMessage();

        int returnid;
        Runtime rt = Runtime.getRuntime();

        try {
            p = rt.exec(cmd);

            //获取进程的标准输出流
            final InputStream console = p.getInputStream();
            //获取进城的错误流
            final InputStream error = p.getErrorStream();
            //启动两个线程，一个线程负责读标准输出流，另一个负责读标准错误流
            new Thread() {
                public void run() {
                    BufferedReader br2 = null;
                    try {
                        br2 = new BufferedReader(new InputStreamReader(error, "utf-8"));
                        String line2 = null;
                        while ((line2 = br2.readLine()) != null) {
                            if (line2 != null) {

                                if (redirct) {
                                    System.err.println(line2);
                                    cmdMsg.setError(line2);
                                    XxlJobLogger.logByfileNoname(outputLog, line2);

                                } else {
                                    cmdMsg.addError(line2);
                                    XxlJobLogger.logByfileNoname(outputLog, line2);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            error.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            new Thread() {
                public void run() {
                    BufferedReader br1 = null;
                    try {
                        br1 = new BufferedReader(new InputStreamReader(console, "utf-8"));
                        String line1 = null;
                        while ((line1 = br1.readLine()) != null) {
                            if (line1 != null) {
                                if (redirct) {
                                    cmdMsg.setOutput(line1);
                                    System.out.println(line1);
                                    XxlJobLogger.logByfileNoname(outputLog, line1);
                                } else {
                                    cmdMsg.addOutput(line1);
                                    XxlJobLogger.logByfileNoname(outputLog, line1);

                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            console.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
            returnid = p.waitFor();
            System.out.println("returnid:" + returnid);
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            returnid = -1;
            if (p != null) {
                close(p.getErrorStream());
                close(p.getInputStream());
                close(p.getOutputStream());
            }
            cmdMsg.addError(e.getMessage());
            XxlJobLogger.logByfileNoname(outputLog, e.getMessage());

        }
        cmdMsg.setCode(returnid);
        return cmdMsg;
    }

    /**
     * 执行cmd 命令返回控制台消息
     *
     * @param cmd
     * @return
     */
    public CmdMessage execCmd(String cmd, String outputLog) {
        return execCmd(cmd, false, outputLog);
    }

    public void close(java.io.Closeable clo) {
        try {
            if (clo != null) {
                clo.close();
            }
        } catch (Exception ee) {
        }
    }

    /**
     * 终止当前进程
     */
    public void kill() {
        if (p != null) {
            p.destroy();
        }
    }

    public CmdMessage unsyncExecCmd(String cmd) {
        return unsyncExecCmd(cmd, false);
    }

    public CmdMessage unsyncExecCmd(String cmd, final boolean redirct) {
        final CmdMessage cmdMsg = new CmdMessage();

        int returnid = 0;
        Runtime rt = Runtime.getRuntime();

        try {
            p = rt.exec(cmd);

            //获取进程的标准输出流
            final InputStream console = p.getInputStream();
            //获取进城的错误流
            final InputStream error = p.getErrorStream();
            //启动两个线程，一个线程负责读标准输出流，另一个负责读标准错误流
            new Thread() {
                public void run() {
                    BufferedReader br2 = null;
                    try {
                        br2 = new BufferedReader(new InputStreamReader(error, "utf-8"));
                        String line2 = null;
                        while ((line2 = br2.readLine()) != null) {
                            if (line2 != null) {

                                if (redirct) {
                                    System.err.println(line2);
                                    cmdMsg.setError(line2);
                                } else {
                                    cmdMsg.addError(line2);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            error.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            new Thread() {
                public void run() {
                    BufferedReader br1 = null;
                    try {
                        br1 = new BufferedReader(new InputStreamReader(console, "utf-8"));
                        String line1 = null;
                        while ((line1 = br1.readLine()) != null) {
                            if (line1 != null) {
                                if (redirct) {
                                    cmdMsg.setOutput(line1);
                                    System.out.println(line1);
                                } else {
                                    cmdMsg.addOutput(line1);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            console.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            returnid = -1;
            if (p != null) {
                close(p.getErrorStream());
                close(p.getInputStream());
                close(p.getOutputStream());
            }
            cmdMsg.addError(e.getMessage());
        }
        cmdMsg.setCode(returnid);
        return cmdMsg;
    }
}


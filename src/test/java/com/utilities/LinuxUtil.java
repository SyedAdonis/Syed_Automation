package com.utilities;

import com.jcraft.jsch.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;

public class LinuxUtil {

    public Session linuxSession(String hostName, Integer portNumber) {
        JSch linux = new JSch();
        Session session = null;
        String userName = System.getProperty("user.name");
        String privateKeyPath = "Users/"+userName+ "/.ssh/id_rsa";
        try {
            linux.addIdentity(privateKeyPath);
            session = linux.getSession(userName, hostName, portNumber);
            session.setConfig("PreferredAuthentications","publickey,keyboard-interactive,password");
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
        } catch (JSchException e)
        {
            throw new RuntimeException("Failed to create JSch Session object.", e);
        }
        return session;
    }

    public Channel linuxExecChannel (Session session) {
        String command = "pwd";
        Channel channel = null;

        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            ((ChannelExec) channel).setPty(false);
            channel.connect();
        } catch (JSchException e) {
            throw new RuntimeException("Error during SSH command execution. Command: "+ command);
        }
        return channel;
    }

    public Channel linuxSHellChannel(Session session, ArrayList<String> commands) {

        Channel channel = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            channel = session.openChannel("shell");
            channel.setOutputStream(out);
            PrintStream shellStream = new PrintStream(channel.getOutputStream());
            channel.connect();
        for(String command : commands)
        {
            shellStream.println(command);
            shellStream.flush();
        }
        shellStream.close();
        Thread.sleep(2000);
        } catch (JSchException js) {
            System.out.println("Error while opening Channel: " + js);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

       return channel;
    }

    public ChannelSftp linuxSftpChannel(Session session,String path, String destination) {

        ChannelSftp channelsftp = null;
    try {
        channelsftp = (ChannelSftp) session.openChannel("sftp");
        channelsftp.connect();
        channelsftp.put(path, destination);
        Thread.sleep(2000);
    } catch (JSchException js) {
        System.out.println("Error while opening the channel. "+ js.getMessage());
    } catch (SftpException sf) {
        System.out.println("Error while creating sftpchannel. "+sf.getMessage());
    } catch (Exception e)
    {
        System.out.println(e.getStackTrace());
    }
    return channelsftp;
    }

    public void disconnectLinux(Session session, Channel channel, ChannelSftp channelSftp) {
        if(channelSftp.isConnected()) {
            channelSftp.disconnect();
        }
        if(channel.isConnected()) {
            channel.disconnect();
        }
        if(session.isConnected()) {
            session.disconnect();
        }
    }
}

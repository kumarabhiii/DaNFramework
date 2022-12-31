package com.thinking.machines.nframework.server;
import com.thinking.machines.nframework.server.annotations.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.*;
public class NFrameworkServer
{
private ServerSocket serverSocket;
private Set<Class> tcpNetworkServiceClasses;
public NFrameworkServer()
{
tcpNetworkServiceClasses=new HashSet<>();
}
public void registerClass(Class c)
{
tcpNetworkServiceClasses.add(c);
}

public TCPService getTCPService(String path)
{
Path pathOnType;
Path pathOnMethod;
Method methods[];
String fullPath;
TCPService tcpService=null;
for(Class c:tcpNetworkServiceClasses)
{
pathOnType=(Path)c.getAnnotation(Path.class);
if(pathOnType==null) continue;
methods=c.getMethods();
for(Method method:methods)
{
pathOnMethod=(Path)c.getAnnotation(Path.class);
if(pathOnMethod==null) continue;
fullPath=pathOnType.value()+pathOnMethod.value();
if(path.equals(fullPath))
{
tcpService=new TCPService();
tcpService.c=c;
tcpService.method=method;
tcpService.path=path;
return tcpService;
}
}
}
return null;
}

public void start()
{
try
{
serverSocket=new ServerSocket(5500);
Socket socket;
RequestProcessor requestProcessor;
while(true)
{
socket=serverSocket.accept();
requestProcessor=new RequestProcessor(this,socket);
}
}catch(Exception e)
{
System.out.println(e);
}
}
}
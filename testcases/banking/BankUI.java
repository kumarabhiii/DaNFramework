		//eg377.java

import com.kumar.abhiii.nframework.client.*;
class BankUI
{
public static void main(String gg[])
{
NFrameworkClient client=new NFrameworkClient();
try
{
String branchName=(String)client.execute("/banking/branchName",gg[0]);
System.out.println(branchName);
}catch(Throwable t)
{
System.out.println(t.getMessage());
}
}
}
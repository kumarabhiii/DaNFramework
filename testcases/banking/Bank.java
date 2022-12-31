		//eg376.java

import com.kumar.abhiii.nframework.server.*;
import com.kumar.abhiii.nframework.server.annotations.*;
@Path("/banking")
public class Bank
{
@Path("/branchName")
public String getBranchName(String city) throws BankingException
{
System.out.println("Method got called");
if(city.equals("UJJAIN"))
{
return "Freeganj";
}
if(city.equals("MUMBAI"))
{
return "Colaba";
}
throw new BankingException("No branch in that city");
}
public static void main(String gg[])
{
NFrameworkServer server=new NFrameworkServer();
server.registerClass(Bank.class);
server.start();
}
}
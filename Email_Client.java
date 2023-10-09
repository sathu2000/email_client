import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import javax.mail.*;
import javax.mail.internet.*;
//import javax.activation.*;
import javax.mail.Session;
import javax.mail.Transport;

public class Email_Client {
	private ArrayList<Recipient> recipientList;
    private ArrayList<Wishable> birthdayRecipientsList;
    private FileReader fr;
    private BufferedReader bfr;
    public Email_Client(String file) throws FileNotFoundException {
    	fr=new FileReader(file);
    	bfr=new BufferedReader(fr);
    	recipientList=new ArrayList<Recipient>();
    	birthdayRecipientsList=new ArrayList<Wishable>();
    }
    public void update() throws IOException {
	    String i;
	    while ((i=bfr.readLine())!=null) {
	    	String[] temp=i.split(":");
	  	  if (temp[0].equals("Personal") ){
	  		  PersonalRecipient rec=new PersonalRecipient(temp[1].split(",")[0].replaceAll("\\s+",""),temp[1].split(",")[1],temp[1].split(",")[2],temp[1].split(",")[3]);
	  		  recipientList.add(rec);
	  		  birthdayRecipientsList.add(rec);
	  	  }
	  	  else if (temp[0].equals("OfficeFriend")){
			  OfficeFriendRecipient rec=new OfficeFriendRecipient(temp[1].split(",")[0].replaceAll("\\s+",""),temp[1].split(",")[1],temp[1].split(",")[2],temp[1].split(",")[3]);
			  recipientList.add(rec);
			  birthdayRecipientsList.add(rec);
		  }
	  	  else if (temp[0].equals("Official")){
			  OfficialRecipient rec=new OfficialRecipient(temp[1].split(",")[0].replaceAll("\\s+",""),temp[1].split(",")[1],temp[1].split(",")[2]);
			  recipientList.add(rec);
		  }
	    }
	   }
    public ArrayList<Wishable> getBirthdayList(){
    	return this.birthdayRecipientsList;
    }
    public ArrayList<Recipient> getRecipientList(){
    	return this.recipientList;
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter option type: \n"
                  + "1 - Adding a new recipient\n"
                  + "2 - Sending an email\n"
                  + "3 - Printing out all the recipients who have birthdays\n"
                  + "4 - Printing out details of all the emails sent\n"
                  + "5 - Printing out the number of recipient objects in the application");

            int option = scanner.nextInt();
            
            Email_Client ec=new Email_Client("C:\\Users\\DELL\\Desktop\\email_client\\Client_List.txt");
            ec.update();
      	  	ArrayList<Wishable> bdayList=ec.getBirthdayList();
      	  	ArrayList<Recipient> allRecipients=ec.getRecipientList();
    
            switch(option){
            
                  case 1:
                      // input format - Official: nimal,nimal@gmail.com,ceo
					  System.out.println("input format - Official: nimal,nimal@gmail.com,ceo");
                      // Use a single input to get all the details of a recipient
                	  Scanner scanner1=new Scanner(System.in);
                	  String input=scanner1.nextLine();
                	  //BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
                	  RecipientFile clientList=new RecipientFile("C:\\Users\\DELL\\Desktop\\email_client\\Client_List.txt");
                	  clientList.update(input);
                      // code to add a new recipient
                      // store details in clientList.txt file
                      // Hint: use methods for reading and writing files
                      break;
                  case 2:
                      // input format - email, subject, content
					  System.out.println("input format - email, subject, content");
                	  String[] temp=(scanner.next()).split(",");
                      // code to send an email
                	  SendEmail mail=new SendEmail(temp[0],temp[1],temp[2]);
                	  mail.send();
                	  FileOutputStream fos=new FileOutputStream("C:\\Users\\DELL\\Desktop\\email_client\\Email_Sent.txt");
                	  ObjectOutputStream oos=new ObjectOutputStream(fos);
                	  oos.writeObject(mail);
                	  System.out.println("Mail Sent");
                	  break;
                  case 3:
                	// input format - yyyy/MM/dd (ex: 2018/09/17)
					System.out.println("input format - yyyy/MM/dd (ex: 2018/09/17)");

                	  String date=scanner.next();
                	  LocalDate datel=LocalDate.of(Integer.valueOf(date.split("/")[0]),Integer.valueOf(date.split("/")[1]),Integer.valueOf(date.split("/")[2]));
                	  System.out.println("Recipients who have birthday on "+date+" are");
                	  for (Wishable rec:bdayList ) {
                		  if ((rec.get_dob().toString().substring(5)).equals(datel.toString().substring(5))) {
                			  System.out.println(rec.getName());
                		  }
                	  }
                      // code to print recipients who have birthdays on the given date
                      break;
                  case 4:
                	  // code to print the details of all the emails sent on the input date
                      // input format - yyyy/MM/dd (ex: 2018/09/17)
                	  String date1=scanner.next();
                	  FileInputStream fis=new FileInputStream("C:\\Users\\DELL\\Desktop\\email_client\\Email_Sent.txt");
                	  ObjectInputStream ois=new ObjectInputStream(fis);
                	  SendEmail sent;
                	  try {
                		  while (true) {
                			  sent=(SendEmail) ois.readObject();
                			  if (sent.getSentTime().equals(LocalDate.of(Integer.valueOf(date1.split("/")[0]),Integer.valueOf(date1.split("/")[1]),Integer.valueOf(date1.split("/")[2])))){
                				  System.out.println("To: "+sent.getAddress()+", Subject: "+sent.getSubject()+", Content: "+sent.getBody());
                			  }
                		  }
                	  }
                	  catch(EOFException e){
                		  ;
                	  }
                      
                      break;
                  case 5:
                	  System.out.println("Number Of Recipient Objects in the application is "+Recipient.getNoOfRecipient());
                      // code to print the number of recipient objects in the application
                      break;

            }
            
            for (Wishable w:bdayList) {
            	if (w.get_dob().equals(LocalDate.now())) {
            		w.sendBirthdayWish();
            	}
            }
            scanner.close();
        }
}

//
abstract class Recipient{
	private String name;
	private String address;
	private static int noOfRecipient=0;
	Recipient(String name,String address){
		this.name=name;
		this.address=address;
		noOfRecipient++;
	}
	public void setName(String name) {
		this.name=name;
	}
	public void setAddress(String address) {
		this.address=address;
	}
	public String getName() {
		return this.name;
	}
	public String getAddress() {
		return this.address;
	}
	public static int getNoOfRecipient() {
		return noOfRecipient;
	}
}
//
class PersonalRecipient extends Recipient implements Wishable{
	private String nickName;
	private LocalDate dob;
	PersonalRecipient(String name,String nickName, String address,String dob) {
		super(name, address);
		this.nickName=nickName;
		this.dob=LocalDate.of(Integer.valueOf(dob.split("/")[0]),Integer.valueOf(dob.split("/")[1]),Integer.valueOf(dob.split("/")[2]));
	}
	public void setNickName(String nickName) {
		this.nickName=nickName;
	}
	public void set_dob(String dob) {
		this.dob=LocalDate.of(Integer.valueOf(dob.split("/")[0]),Integer.valueOf(dob.split("/")[1]),Integer.valueOf(dob.split("/")[2]));
	}
	public String getNickName() {
		return nickName;
	}
	public LocalDate get_dob() {
		return dob;
	}
	public void sendBirthdayWish() throws IOException {
		SendEmail se=new SendEmail(this.getAddress(),"BirthDay Wish","hugs and love on your birthday. Sathurgini for personal recipients");
		se.send();
		FileOutputStream fos=new FileOutputStream("C:\\Users\\DELL\\Desktop\\email_client\\Email_Sent.txt");
  	  	ObjectOutputStream oos=new ObjectOutputStream(fos);
  	  	oos.writeObject(se);
  	  	oos.close();
  	  	System.out.println("Birthday Wish sent to "+this.getName());
	}
}
//
class OfficialRecipient extends Recipient{
	private String designation;
	OfficialRecipient(String name,String address,String designation){
		super(name,address);
		this.designation=designation;
	}
	public void setDesignation(String designation) {
		this.designation=designation;
	}
	public String getDesignation() {
		return this.designation;
	}
}
//
class OfficeFriendRecipient extends OfficialRecipient implements Wishable{
	private LocalDate dob;
	OfficeFriendRecipient(String name, String address, String designation,String dob) {
		super(name, address, designation);
		this.dob=LocalDate.of(Integer.valueOf(dob.split("/")[0]),Integer.valueOf(dob.split("/")[1]),Integer.valueOf(dob.split("/")[2]));
	}
	public void set_dob(String dob) {
		this.dob=LocalDate.of(Integer.valueOf(dob.split("/")[0]),Integer.valueOf(dob.split("/")[1]),Integer.valueOf(dob.split("/")[2]));
	}
	public LocalDate get_dob() {
		return dob;
	}
	public void sendBirthdayWish() throws IOException {
		SendEmail se=new SendEmail(this.getAddress(),"BirthDay Wish","Wish you a Happy Birthday. Sathurgini");
		se.send();
		FileOutputStream fos=new FileOutputStream("C:\\Users\\DELL\\Desktop\\email_client\\Email_Sent.txt");
  	  	ObjectOutputStream oos=new ObjectOutputStream(fos);
  	  	oos.writeObject(se);
  	  	oos.close();
  	  	System.out.println("Birthday Wish sent to "+this.getName());
	}
}
//
interface Wishable{
	String getName();
	String getAddress();
	LocalDate get_dob();
	void sendBirthdayWish() throws IOException;
}
//
class RecipientFile{
	private String file;
	public RecipientFile(String file) {
		this.file=file;
	}
	public void update(String details) throws IOException {
		FileWriter fw=new FileWriter(file,true);
		BufferedWriter bfw=new BufferedWriter(fw);
		bfw.append(details+"\n");
		bfw.close();
	}
}
//
class SendEmail implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final String username = "user@gmail.com";
    final String password = "password";
	private String recipient;
	private String subject;
	private String body;
	private LocalDate sentTime;
	SendEmail(String recipient,String subject,String body){
		this.recipient=recipient;
		this.subject=subject;
		this.body=body;
		sentTime=LocalDate.now();
	}
	public void send() {
        Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); //TLS
        
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient)
            );
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);


        } catch (MessagingException e) {
            e.printStackTrace();
        }
	}
	
	public LocalDate getSentTime() {
		return sentTime;
	}
	public String getAddress() {
		return recipient;
	}
	public String getSubject() {
		return subject;
	}
	public String getBody() {
		return body;
	}
}

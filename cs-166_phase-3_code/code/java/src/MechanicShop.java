/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class MechanicShop{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public MechanicShop(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		while(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + MechanicShop.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		MechanicShop esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new MechanicShop (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. AddCustomer");
				System.out.println("2. AddMechanic");
				System.out.println("3. AddCar");
				System.out.println("4. InsertServiceRequest");
				System.out.println("5. CloseServiceRequest");
				System.out.println("6. ListCustomersWithBillLessThan100");
				System.out.println("7. ListCustomersWithMoreThan20Cars");
				System.out.println("8. ListCarsBefore1995With50000Milles");
				System.out.println("9. ListKCarsWithTheMostServices");
				System.out.println("10. ListCustomersInDescendingOrderOfTheirTotalBill");
				System.out.println("11. < EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: AddCustomer(esql); break;
					case 2: AddMechanic(esql); break;
					case 3: AddCar(esql); break;
					case 4: InsertServiceRequest(esql); break;
					case 5: CloseServiceRequest(esql); break;
					case 6: ListCustomersWithBillLessThan100(esql); break;
					case 7: ListCustomersWithMoreThan20Cars(esql); break;
					case 8: ListCarsBefore1995With50000Milles(esql); break;
					case 9: ListKCarsWithTheMostServices(esql); break;
					case 10: ListCustomersInDescendingOrderOfTheirTotalBill(esql); break;
					case 11: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	//This function adds a customer in the database system
        //written by Matthew Walsh      
        public static void AddCustomer(MechanicShop esql) throws SQLException{//1

                //recieves customer's full name
                System.out.println("Enter Customer First Name:");
                String fname = "";
                try{
                                                fname = in.readLine();
                }catch(Exception e1){
                                                System.out.println("Invalid input");

                }

                System.out.println("Enter Customer Last Name:");
                String lname = "";
                try{
                                                lname = in.readLine();
                }catch(Exception e1){
                                                System.out.println("Invalid input");

                }

                //checks amount of rows in Customers and automatically assigns id
                int cust_id = esql.executeQuery("SELECT * FROM Customer");

                //get customer phone number
                System.out.println("Enter Customer Phone Number:");
                String phone_num_s = "";
                try{
                                                phone_num_s = in.readLine();
                }catch(Exception e1){
                                                System.out.println("Invalid input");

                }

                //recieve customer address
                System.out.println("Enter Customer Address:");
                String addr = "";
                try{
                                                addr = in.readLine();
                }catch(Exception e1){
                                                System.out.println("Invalid input");

                }

                //execute SQL statements
                esql.executeUpdate("INSERT INTO Customer VALUES (" + cust_id + ",'" + fname + "' , '" + lname + "', '" + phone_num_s + "' , '" + addr + "');");
                esql.executeQueryAndPrintResult("Select * from Customer;");
        }
	
	//This function adds a mechanic
        //written by Raajitha Rajkumar
	public static void AddMechanic(MechanicShop esql) throws SQLException{//2
		//takes in any name as input
                System.out.println("Enter Mechanic's First Name:");
                String fname = "";
                try{
                                fname = in.readLine();
                }catch(Exception e1){
                                System.out.println("Invalid input");

                }
                System.out.println("Enter Mechanic's Last Name:");
                String lname = "";
                try{
                                lname = in.readLine();
                }catch(Exception e1){
                                System.out.println("Invalid input");

                }
                //this function returns the rows of mechanics and corresponds to the ID, if there are 501 rows, then the id will be 501 and assigns it to mech_id
                int mech_id = esql.executeQuery("SELECT * FROM Mechanic");
                //gets experience as input
                System.out.println("Enter the Mechanic's years of experience:");
                int mech_exp = readChoice();
                //this assures that the years of experience are within bounds
                while(mech_exp > 99){
                        System.out.println("Cannot have that many years of experience, please list a year less than that");
                        mech_exp = readChoice();
                }
                System.out.println("The mechanic's id is: " + mech_id);
                esql.executeUpdate("INSERT INTO Mechanic VALUES (" + mech_id + ",'" + fname + "' , '" + lname + "'," + mech_exp+ ");");
                esql.executeQueryAndPrintResult("Select * from Mechanic;");
	}
	
	public static void AddCar(MechanicShop esql) throws SQLException{//3
		System.out.println("Enter the car's VIN:");
		String vin = "";
		try{
				vin = in.readLine();
		}catch(Exception e1){
				System.out.println("Invalid input");

		}
		System.out.println("Enter Car's Make:");
		String make = "";
		try{
				make = in.readLine();
		}catch(Exception e1){
				System.out.println("Invalid input");

		}
		System.out.println("Enter Car's Model:");
		String model = "";
		try{
				model = in.readLine();
		}catch(Exception e1){
				System.out.println("Invalid input");

		}
		System.out.println("Enter Car's year");
		int year = readChoice();
		esql.executeUpdate("INSERT INTO Car VALUES ('" + vin + "','" + make + "' , '" + model + "'," + year + ");");
		esql.executeQueryAndPrintResult("Select * from Car;");
	}
	
	//This function makes a new sercvice request for new customers or old
        //This function checks to make sure if it is a returning customer or new customer
        //This function allows to choose an existing car for returning customers
        //Written by Raajitha Rajkumar
        public static void InsertServiceRequest(MechanicShop esql) throws SQLException{//4

                //Get customer's last name
                System.out.println("Enter Customer's Last Name:");
                String lname = "";
                try{
                                lname = in.readLine();
                }catch(Exception e9){
                                System.out.println("Invalid input");

                }
                String cap = lname.substring(0, 1).toUpperCase() + lname.substring(1);
		
		//checks if there are rows existing with that last name
                int rows = esql.executeQuery("SELECT * FROM Customer  WHERE (lname = '" + cap + "')");

                //if there are rows, checks if it is a returning customer, otherwise get's added into the database
                if(rows > 0){
                                System.out.println("That last name is in our file! Please check your corresponsding customer id");
                                esql.executeQueryAndPrintResult("Select * from Customer WHERE (lname = '" + cap + "')");
                                System.out.println("Are you a returning customer (yes/no)?");

                                String returning = "";
                                try{
                                                returning = in.readLine();
                                }catch(Exception e1){
                                                System.out.println("Invalid input");
                                }
                                if(returning.equals("yes")){
                                                esql.executeQueryAndPrintResult("Select * from Customer WHERE (lname = '" + cap + "')");
                                }else if(returning.equals("no")){
                                                System.out.println("You have not been added to the system yet, please continue with adding your info and car info.");
                                                AddCustomer(esql);
                                                AddCar(esql);
                                                System.out.println("You have now been added to the database!");
                                                esql.executeQueryAndPrintResult("Select * from Customer WHERE (lname = '" + cap + "')");
                                }
                }else{
                        System.out.println("You have not been added to the system yet, please continue with adding your info and car info.");
                        AddCustomer(esql);
                        AddCar(esql);
                        System.out.println("You have now been added to the database!");
                        esql.executeQueryAndPrintResult("Select * from Customer WHERE (lname = '" + cap + "')");
                }

                //get's customer id
                int custid = 0;
                int check = 0;
                //checks if id is valid
                while(check < 1){
                        System.out.println("Print a valid customer id:");
                        custid = readChoice();
                        check = esql.executeQuery("SELECT * FROM Customer WHERE (id = " + custid +" AND lname = '" + cap + "')");
                }

                //prints cars owned by customer
                esql.executeQueryAndPrintResult("Select C.vin, C.make, C.model FROM Car C, Customer C1, Owns O WHERE (O.customer_id = " + custid + " AND C1.id = " + custid + " AND O.car_vin = C.vin)");
                System.out.println("Your cars are displayed above with the corresponding vin");
		
		//get's vin as input, and checks if it's valid
                check = 0;
                String car = "";
                while(check < 1){
                        System.out.println("Select which car you'd like to make a service request on by typing in the correct vin:");
                        String c = "";
                        try{
                                                c = in.readLine();
                        }catch(Exception e10){
                                                System.out.println("Invalid input");

                        }
                        car = c.toUpperCase();
                        check = esql.executeQuery("SELECT * FROM Car WHERE (vin = '" + car + "')");
                }

                //get's date
                System.out.println("What is the date?");
                String currdate = "";
                try{
                                currdate = in.readLine();
                }catch(Exception e9){
                                System.out.println("Invalid input");

                }

                //get's odometer reading
                System.out.println("What is the odometer reading of the car (print only digits)?");
                int odometer = readChoice();
                System.out.println("What is the complaint you have for your car?");
                String complaint = "";
                try{
                                                complaint = in.readLine();
                }catch(Exception e10){
                                                System.out.println("Invalid input");

                }

                //checks rows of requests and assigns corresponding id
                int rid = esql.executeQuery("SELECT * FROM Service_Request");

                //executes statements
                esql.executeUpdate("INSERT INTO Service_Request VALUES(" + rid + "," + custid + ",'" + car + "','" + currdate + "', " + odometer + ",'" + complaint +"')");
                esql.executeQueryAndPrintResult("SELECT * FROM Service_Request WHERE (rid = " + rid + ")");

        }
		
	//This function creates a Closed Service Request
        //This function requires a RID and mechanic ID 
        //This function also checks whether the mechanic id and rid are valid
        //written by Matthew Walsh and Raajitha Rajkumar
        public static void CloseServiceRequest(MechanicShop esql) throws SQLException{//5

                int rid = 0;
                int mid = 0;

                //asks for rid as input and checks if it is valid
                int check = 0;
                while(check < 1){
                        System.out.println("Enter a valid Service Request RID (if we keep asking, the id is not valid):");
                        rid = readChoice();
                        check = esql.executeQuery("SELECT * FROM Service_Request WHERE (rid = " + rid + ")");
                }

                //asks for mechanic id and checks if it is valid
                check = 0;
                while(check < 1){
                        System.out.println("Enter a valid Mechanic ID that worked on your car (if we keep asking, the id is not valid):");
                        mid = readChoice();
                        check = esql.executeQuery("SELECT * FROM Mechanic WHERE (id = " + mid + ")");
                }

                //checks the rows of Closed Requests and automatically assigns a WID
                int wid = esql.executeQuery("SELECT * FROM Closed_Request;");
                wid = wid + 1;

                //recieves date and checks if it is valid
                boolean NOdate = true;
                String currdate = "";
                while(NOdate){
                        System.out.println("What is the date (MUST BE IN FORMAT month-day-year)?");
                        try{
                                currdate = in.readLine();
                        }catch(Exception e9){
                                System.out.println("Invalid input");

                        }
                        if(currdate.charAt(2) == '-' && currdate.charAt(5) == '-' && currdate.length() == 10){
                                NOdate = false;
                        }else{
                                NOdate = true;
                        }
                }
		
		//Recieves any closing comments
                System.out.println("Any comments?");
                String comment = "";
                try{
                                comment = in.readLine();
                }catch(Exception e9){
                                System.out.println("Invalid input");

                }

                //Asks for final bill
                System.out.println("what is the bill (MUST BE DIGITS)?");
                int bill = readChoice();

                //executes SQL statements 
                esql.executeUpdate("INSERT INTO Closed_Request VALUES("+wid+"," + rid + "," + mid + ",'" +currdate + "','" + comment + "', " + bill + ")");
                esql.executeQueryAndPrintResult("SELECT * FROM Closed_Request WHERE (wid = " + wid + ")");

        }
	
	// List date, comment, and bill for all closed requests with bill lower than 100
	// written by Raajitha Rajkumar
	public static void ListCustomersWithBillLessThan100(MechanicShop esql) throws SQLException{//6
		esql.executeQueryAndPrintResult("SELECT C.id, C.fname, SUM(CR.bill) FROM Customer C, Closed_Request CR, Service_Request SR WHERE CR.rid = SR.rid AND SR.customer_id = C.id GROUP BY C.id HAVING SUM(CR.bill) < 100");
	}
	
	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){//7
		esql.executeQueryAndPrintResult("SELECT C.make, C.model, C.year FROM Car C, Service_Request SR WHERE C.year <= 1995 AND C.vin = SR.car_vin AND SR.odometer < 50000 GROUP BY C.vin");
	}
	
	//List Make, Model, and Year of all cars build before 1995 having less than 50000 miles
	//written by Raajitha Rajkumar
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8
		esql.executeQueryAndPrintResult("SELECT C.make, C.model, C.year FROM Car C, Service_Request SR WHERE C.year <= 1995 AND C.vin = SR.car_vin AND SR.odometer < 50000 GROUP BY C.vin");
	}
	
	public static void ListKCarsWithTheMostServices(MechanicShop esql)throws Exception{//9
                System.out.println("Enter minimum number of services: ");
                int k;
                String k_s = "";
                try{
                                k_s = in.readLine();
                }catch(Exception e1){
                                System.out.println("Invalid input");

                }
                k = Integer.parseInt(k_s);
                esql.executeQueryAndPrintResult("SELECT CAR.make, CAR.model, COUNT(SR.rid) FROM Car CAR, Service_Request SR WHERE CAR.vin = SR.car_vin GROUP BY CAR.vin HAVING COUNT(SR.rid) > " + k);
		
		
	}
	
	//List the first name, last name and total bill of customers in descending order of
	//their total bill for all cars brought to the mechanic
	//written by Raajitha Rajkumar
	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//10
		esql.executeQueryAndPrintResult("SELECT C.fname, C.lname, SUM(CR.bill) FROM Customer C, Closed_Request CR, Service_Request SR WHERE CR.rid = SR.rid AND SR.customer_id = C.id GROUP BY C.id ORDER BY SUM(CR.bill) DESC");
		
	}
	
}

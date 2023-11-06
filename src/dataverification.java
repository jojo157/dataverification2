import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class dataverification {

    public static void main(String[] args) {
        BufferedReader inputStream = null;
        FileWriter outputStream = null;
        ArrayList<pensioner> members = new ArrayList<pensioner>();
        readInMembers(inputStream, "One", members);
        readInMembers(inputStream, "Two", members);
        readInMembers(inputStream, "Three", members);
        String outputHeaders = createOutputHeaders(inputStream);
        createOutputFile(members, outputStream, outputHeaders);
        System.out.println("Output file created.");
    }

    public static void readInMembers(BufferedReader inputStream, String fileNumber, ArrayList<pensioner> members){
        try {
            String filename = getInputFile(fileNumber);
            inputStream = new BufferedReader(new FileReader(filename));
            String l;
            int count = 0;
            ArrayList<String> columns = new ArrayList();
            while ((l = inputStream.readLine()) != null) {
                count ++;
                String[] values = l.trim().split(",", -2);
                for(int i=0; i< values.length; i++){
                            values[i] = removeUTF8BOM(values[i]);
                            values[i] = values[i].trim();
                }



                if(count == 1){
                    //read columns
                    columns = new ArrayList<String>(Arrays.asList(values));
                }
                if(count>1){
                    mapValuesToPensionerObject(values, fileNumber, members, columns);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        } finally{
            try{
                inputStream.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static String getInputFile(String fileNumber) throws FileNotFoundException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Select " + fileNumber + " file to read in \n");
        String filename = scan.nextLine();
        return filename;
    }

    public static void mapValuesToPensionerObject(String[] values, String inputReportNumber,
                                                  ArrayList<pensioner> members, ArrayList<String> columns ){

        //conditional logic for each input report
        if(inputReportNumber.equals("One")){
            pensioner pens = new pensioner();
            pens.setSchemeID(values[columns.indexOf("Person Reference")]);
            pens.setPPSN(values[columns.indexOf("Pps Number")]);
            pens.setFirstName(values[columns.indexOf("Forename")]);
            pens.setSurname(values[columns.indexOf("Surname")]);
            pens.setSalutation(values[columns.indexOf("Title")]);
            pens.setDateOfBirth(values[columns.indexOf("Date Of Birth")]);
            pens.setPensionerStatus(values[columns.indexOf("Pay Scale Code And Description")]);
            pens.setGender(values[columns.indexOf("Gender")]);
            pens.setMaritalStatus(values[columns.indexOf("Marital Status")]);
            pens.setHomeAddressLine1(values[columns.indexOf("Home Address Line 1")]);
            pens.setHomeAddressLine2(values[columns.indexOf("Home Address Line 2")]);
            pens.setHomeAddressLine3(values[columns.indexOf("Home Address Line 3")]);
            pens.setHomeAddressLine4(values[columns.indexOf("Home Address Line 4")] + " " + values[columns.indexOf("Home Address Line 5")]);
            pens.setPensionCommencementDate(values[columns.indexOf("Date Started")]);
            pens.setPaymentType(values[columns.indexOf("Pay Method")]);
            pens.setNextOfKinComments(values[columns.indexOf("Next Of Kin Comments")]);
            pens.setNextOfKinForename(values[columns.indexOf("Next Of Kin Forename")]);
            pens.setNextOfKinSurname(values[columns.indexOf("Next Of Kin Surname")]);
            pens.setNextOfKinRelationship(values[columns.indexOf("Next Of Kin Relationship")]);
            pens.setPayDateInMonthCurrentlyPaid(values[columns.indexOf("Pay Path Day Number")]);
            members.add(pens);
        }
        else if(inputReportNumber.equals("Two")){
            //check if exists record
            for(pensioner member : members){
                if(member.getSchemeID().equals(values[columns.indexOf("Person Reference")])){
                    //member exists - lets add to them
                    if(columns.contains("4000")){
                        member.setBridgingPension("No");
                        member.setLifetimePensionPerMonth(values[columns.indexOf("4000")]);
                    }else{
                        member.setBridgingPension("Yes");
                    }

                    if(columns.contains("5030") && (parseDoubleOrZero(values[columns.indexOf("5030")]) )<0.0 ){
                        member.setLocalPropertyTaxDeduction("Yes");
                    }else{
                        member.setLocalPropertyTaxDeduction("No");
                    }


                    if((columns.contains("6200") && parseDoubleOrZero(values[columns.indexOf("6200")]) <0.0) || (columns.contains("6455") && parseDoubleOrZero(values[columns.indexOf("6455")]) <0.0) || (columns.contains("5030") && parseDoubleOrZero(values[columns.indexOf("5030")]) <0.0 )){
                        member.setOtherDeductions("Yes");
                    }else{
                        member.setOtherDeductions("No");
                    }


                }
            }

        }
        else{
            //3rd report
            for(pensioner member : members){
                if(member.getSchemeID().equals(values[columns.indexOf("Person Reference")])) {
                    //member exists - lets add to them
                int position = columns.indexOf("Narrative");
                member.setIncreaseEscalationType(values[position]);

                if(values[position].equalsIgnoreCase("Insured")){
                    member.setPensionsPaidByThirdPartyProvider("Insured");
                    member.setCurrentPayrollProvider("Insured");
                }else{
                    member.setCurrentPayrollProvider("Mercer");
                }

                }


            }
        }
    }

    public static String createOutputHeaders(BufferedReader inputStream){
        String columns="";
        try {
            inputStream = new BufferedReader(new FileReader("src/headers.csv"));
            String l;

            while ((l = inputStream.readLine()) != null) {
               columns = l;
            }
        }catch(IOException e){
            e.printStackTrace();
        } finally{
            try{
                inputStream.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return columns;
    }

    public static void createOutputFile(ArrayList<pensioner> members, FileWriter outputStream, String outputHeaders){
        try {
            outputStream = new FileWriter("OutputFiles/output.csv");
            outputStream.write(outputHeaders + "\r\n" );
            for (pensioner member : members) {
                outputStream.write(member.toString() + "\r\n" );

            }
        }
         catch(IOException e){
            e.printStackTrace();
        } finally{
            try{
                outputStream.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static double parseDoubleOrZero(Object value){
        try{
         double  convertedValue = Double.parseDouble((String) value);
            return convertedValue;

        }catch (NumberFormatException e){
            return 0.0;
        }

    }

    public static String removeUTF8BOM(String s) {
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        return s;
    }



}

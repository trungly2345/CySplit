package coms309.people;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class    BankController {

    // Note that there is only ONE instance of PeopleController in 
    // Springboot system.
    HashMap<String, Bank> bankList = new  HashMap<>();
    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // THIS IS THE LIST OPERATION
    // gets all the people in the list and returns it in JSON format
    // This controller takes no input. 
    // Springboot automatically converts the list to JSON format 
    // in this case because of @ResponseBody
    // Note: To LIST, we use the GET method
    @GetMapping("/Bank")
    public  HashMap<String, Bank> Bank() {
        return bankList;
    }

    // THIS IS THE CREATE OPERATION
    // springboot automatically converts JSON input into a person object and 
    // the method below enters it into the list.
    // It returns a string message in THIS example.
    // Note: To CREATE we use POST method
    @PostMapping("/bank")
    public  String createPerson(@RequestBody Bank bank) {
        System.out.println(bank);
        bankList.put(bank.getName(),bank);
        String s = "New bank "+ bank.getName() + " Saved";
        return s;
        //public  ResponseEntity<Map<String, String>>  //unused
        // createPerson(@RequestBody Person person) { // unused
        //Map <String, String> body = new HashMap<>();// unused
        //body.put("message", s); // unused
        //ResponseEntity<>(body, HttpStatus.OK); // unused
    }

  

    // THIS IS THE READ OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We extract the person from the HashMap.
    // springboot automatically converts Person to JSON format when we return it
    // Note: To READ we use GET method
    @GetMapping("/bank/{bank_name}")
    public Bank getBankByName(@PathVariable String bank_name) {
        Bank b = bankList.get(bank_name);
        return b;
    }

    // THIS IS A GET METHOD
    // RequestParam is expected from the request under the key "name"
    // returns all names that contains value passed to the key "name"
    @GetMapping("/bank/public")
    public List<Bank> getPublicBank(@RequestParam("isPublic") boolean isPublic) {
        List<Bank> res = new ArrayList<>();
        for (Bank b : bankList.values()) {
            if (b.isPublic() == isPublic)
                res.add(b);
        }
        return res;
    }

    // THIS IS THE UPDATE OPERATION
    // We extract the person from the HashMap and modify it.
    // Springboot automatically converts the Person to JSON format
    // Springboot gets the PATHVARIABLE from the URL
    // Here we are returning what we sent to the method
    // Note: To UPDATE we use PUT method
    @PutMapping("/bank/{bank_name}")
    public ResponseEntity<Bank> updateBankName(@PathVariable String bank_name, @RequestBody Bank b) {
        Bank existingBank = bankList.get(bank_name);
        if(existingBank == null ){
            return ResponseEntity.notFound().build();
        }
        bankList.remove(bank_name);
        existingBank.setName(b.getName());
        bankList.put(existingBank.getName(),existingBank);

        return ResponseEntity.ok(existingBank);
    }


    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // Note: To DELETE we use delete method
    
    @DeleteMapping("/bank/{bank_name}")
    public ResponseEntity<Bank> deleteBank(@PathVariable String bank_name) {
       Bank existingBank = bankList.get(bank_name);
       if(existingBank == null){
           return ResponseEntity.notFound().build();
       }
       bankList.remove(bank_name);

        return  ResponseEntity.ok(existingBank);
    }
} // end of people controller


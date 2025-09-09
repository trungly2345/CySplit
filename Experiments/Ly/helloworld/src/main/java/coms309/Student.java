package coms309;
public class Student {
    String name;
    String age;
    String classification;


    public Student (String name, String age, String classification){
        this.name = name;
        this.age = age;
        this.classification = classification;
    }


    public String toString(){
        return "Name: " + name + "\n" + "Age: " + age +"\n" + "Classification + " + classification;
    }


}
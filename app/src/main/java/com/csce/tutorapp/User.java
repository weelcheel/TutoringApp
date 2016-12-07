package com.csce.tutorapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by willh_000 on 10/19/2016.
 *
 * Stores the information about the user for easy database entry.
 *
 */

public class User implements Parcelable {

    /* user information variables */
    private String id; //same UID that Firebase assigns users
    private String firstName;
    private String lastName;
    private String email;
    private String about;

    private String accountType; //whether or not the user is a student or a tutor (or both)
    private ArrayList<String> studentSubjects; //what the user wants to be tutored in as a student
    private ArrayList<String> tutorSubjects; //what subjects the user wants to tutor students in

    private ArrayList<String> conversationIDs; //list of conversations this user is currently in

    private float studentRating; //rating from tutors of this user's teachability
    private float tutorRating; //rating from students of this user's teaching skills

    /* whether or not this account has been initialized (user has or hasn't created their profile)*/
    private boolean isProfileCreated;

    /* shouldn't be used except by Firebase */
    public User(){
        studentSubjects = new ArrayList<>();
        tutorSubjects = new ArrayList<>();
        conversationIDs = new ArrayList<>();
    }

    /* construct a new User object from a Firebase id */
    public User(String uid){
        id = uid;
        isProfileCreated = false;
        firstName = "";
        lastName = "";
        email = "";
        accountType = "";
        about = "";

        studentSubjects = new ArrayList<>();
        tutorSubjects = new ArrayList<>();
        conversationIDs = new ArrayList<>();
        studentSubjects.add("test");
        tutorSubjects.add("tutorTest");

        conversationIDs = new ArrayList<>();

        studentRating = 3.f;
        tutorRating = 3.f;
    }

    /* gets the user's id */
    public String getID(){
        return id;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getEmail(){
        return email;
    }

    public String getAccountType(){
        return accountType;
    }

    public String getAbout() { return about; }

    public ArrayList<String> getStudentSubjects(){
        return studentSubjects;
    }

    public ArrayList<String> getTutorSubjects(){
        return tutorSubjects;
    }

    public float getStudentRating(){
        return studentRating;
    }

    public float getTutorRating(){
        return tutorRating;
    }

    public ArrayList<String> getConversationIDs() { return conversationIDs; }

    public boolean getIsProfileCreated() {return isProfileCreated; }

    public void updateSubjects(ArrayList<String> stuSubjects) {studentSubjects = stuSubjects; }

    public void updateAbout(String aboutText) {about = aboutText; }

    /* update user profile minimum*/
    public void updateProfile(String fName, String lName, String actType){
        isProfileCreated = true;

        firstName = fName;
        lastName = lName;
        accountType = actType;
        email = FirebaseUtility.getCurrentFirebaseUser().getEmail();
    }

    /* update all user profile info */
    public void updateProfile(String fName, String lName, String actType, ArrayList<String> stuSubjects,ArrayList<String> tutrSubjects){
        isProfileCreated = true;

        firstName = fName;
        lastName = lName;
        email = FirebaseUtility.getCurrentFirebaseUser().getEmail();
        accountType = actType;
        studentSubjects = stuSubjects;
        tutorSubjects = tutrSubjects;
    }

    //-------------PARCEL INTERFACE-----------------
    public User(Parcel in){
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        accountType = in.readString();
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(id);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(email);
        out.writeString(accountType);
    }

    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>(){
        public User createFromParcel(Parcel in){
            return new User(in);
        }

        public User[] newArray(int size){
            return new User[size];
        }
    };

    public String toString(){
        return firstName + " " + lastName;
    }
}

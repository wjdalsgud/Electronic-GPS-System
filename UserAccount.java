package com.example.test;

// 정민형 : 사용자 회원가입 정보 구현
public class UserAccount
{
    private String idToken; // Firebase UID
    private String emailID;  // 이메일 아이디
    private int password; // 비밀번호
    private  String name;



    public UserAccount() { }
    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }


    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
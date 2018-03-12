public class User {
    public String _username, _password;
    public Socket _client;

    public User(String un, String pw){
        this._username = un;
        this._password = pw;
    }

    public String ToString() {
        return String.format("%s\t%s:%d", 
            this._username, 
            this._client.getInetAddress().toString(), 
            this._client.getPort());
    }
}//end of User class
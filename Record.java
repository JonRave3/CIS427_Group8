/*
Jonnathen Ravelo, Ayesha Saleem
CIS427 Project2
*/

public class Record {
    public String _firstname, _lastname, _phone;
    private final int _recordId;

    public Record(int id) {
        _recordId = id;
    }
    public int getRecordId(){
        return this._recordId;
    }
    public String ToString(){
        return String.format("%d\t%s %s\t%s", getRecordId(), _firstname, _lastname, _phone);
    }
    public String forFile(){
        return String.format("%d %s %s %s\n", getRecordId(), _firstname, _lastname, _phone);
    }
}//end of Record class
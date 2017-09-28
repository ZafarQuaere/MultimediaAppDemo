package zafar.multimediademo.model;

import java.util.ArrayList;

/**
 * Created by Zafar on 08-01-2017.
 */

public class ProfileData {

    private ArrayList<Detail> details;

    public ArrayList<Detail> getDetails() { return this.details; }

    public void setDetails(ArrayList<Detail> details) { this.details = details; }

    private int success;

    public int getSuccess() { return this.success; }

    public void setSuccess(int success) { this.success = success; }
}

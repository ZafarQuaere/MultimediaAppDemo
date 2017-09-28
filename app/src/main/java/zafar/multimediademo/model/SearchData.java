package zafar.multimediademo.model;

import java.util.ArrayList;

/**
 * Created by Zafar on 10-01-2017.
 */

public class SearchData {

    private ArrayList<SearchDetail> details;

    public ArrayList<SearchDetail> getDetails() { return this.details; }

    public void setDetails(ArrayList<SearchDetail> details) { this.details = details; }

    private int success;

    public int getSuccess() { return this.success; }

    public void setSuccess(int success) { this.success = success; }
}

package com.waterR8.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Information about a single 'sequence' 
 * Where a sequence defines a request to 
 * map the network.
 * 
 * 
 * @author casey
 *
 */
public class SequenceInfo {
	
	private Date seqRunDate;
	private int seq;
	private String seqLabel;
	private List<SeqHit> devicesThatResponded = new ArrayList<SeqHit>(); // each src that responded.

	public SequenceInfo() {}
	
	public SequenceInfo(int seq, Date seqRunDate, String seqLabel) {
		this.seq = seq;
		this.seqRunDate = seqRunDate;
		this.seqLabel = seqLabel;
	}

	public String getSeqLabel() {
		return seqLabel;
	}

	public void setSeqLabel(String seqLabel) {
		this.seqLabel = seqLabel;
	}

	public Date getSeqRunDate() {
		return seqRunDate;
	}

	public void setSeqRunDate(Date seqRunDate) {
		this.seqRunDate = seqRunDate;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public List<SeqHit> getDevicesThatResponded() {
		return devicesThatResponded;
	}

	public void setDevicesThatResponded(List<SeqHit> devicesThatResponded) {
		this.devicesThatResponded = devicesThatResponded;
	}
}

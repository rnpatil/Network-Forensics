
import java.nio.charset.Charset;
import java.util.Arrays;

public class Packet {

	private static final String ipEthType="08:00";
	private static final int tcpProtocol=6;
	private String ethType;
	private int iheaderLength;
	private int totalLength;  
	private int protocol;
	private String sourceIp;
	private String destIp;
	private int sourcePort;
	private int destPort;
	private int sequenceNumber;
	private int ackNumber;
	private  int dataOffset;
	private  int dataLength;
	private  String payload="";
	private  long reqtime;


	public Packet(final byte[] data) {
		ethType =  String.format("%02X", data[12]) + ":" + String.format("%02X", data[13]);
		iheaderLength = ((int)data[14] & 0x0F)*4;
		totalLength = toDecimal(Arrays.copyOfRange(data, 16, 18));
		protocol = (int) data[23] & 0xFF;

		if(protocol==tcpProtocol)
		{
			sourceIp=((int) data[26] & 0xFF) + "." + ((int) data[27] & 0xFF) + "." + ((int) data[28] & 0xFF) + "."
					+ ((int) data[29] & 0xFF);
			destIp = ((int) data[30] & 0xFF) + "." + ((int) data[31] & 0xFF) + "." + ((int) data[32] & 0xFF) + "."
					+ ((int) data[33] & 0xFF);
			sourcePort = toDecimal(Arrays.copyOfRange(data, 34, 36));
			destPort = toDecimal(Arrays.copyOfRange(data, 36, 38));
			sequenceNumber = toDecimal(Arrays.copyOfRange(data, 38, 42));
			ackNumber = toDecimal(Arrays.copyOfRange(data, 42, 46));
			dataOffset =((int)(data[46] & 0xFF)>>4)*4; 

			dataLength = totalLength-iheaderLength-dataOffset;
			payload = new String(Arrays.copyOfRange(data,data.length-dataLength, data.length), Charset.forName("ISO-8859-1"));

		} 
	}


	public static int toDecimal(byte[] bytes) {
		int ret = 0;
		for (int i = 0; i < bytes.length; i++) {
			ret <<= 8;
			ret |= (int) bytes[i] & 0xFF;
		}
		return ret;
	}


	public String getEthType() {
		return ethType;
	}

	public int getProtocol() {
		return protocol;
	}

	public int getTotalLength() {
		return totalLength;
	}


	public String getDestIp() {
		return destIp;
	}


	public int getSourcePort() {
		return sourcePort;
	}


	public int getDestPort() {
		return destPort;
	}


	public int getSequenceNumber() {
		return sequenceNumber;
	}


	public int getiHeaderLength() {
		return iheaderLength;
	}


	public int getAckNumber() {
		return ackNumber;
	}


	public int getDataLength() {
		return dataLength;
	}

	public long getReqtime() {
		return reqtime;
	}

	public void setReqtime(long reqtime) {
		this.reqtime = reqtime;
	}

	public String getPayload() {
		return payload;
	}

	public int getDataOffset() {
		return dataOffset;
	}


	public String getSourceIp() {
		return sourceIp;
	}
}

package com.kc.rc;

public class ChannelValues {

	private static final int THRO = 0;
	private static final int YAW = 1;
	private static final int PITCH = 2;
	private static final int ROLL = 3;
	private static final int MODE = 4;

	private int[] channel = new int[8];
	private StringBuffer sb = new StringBuffer();

	public void setValue(int index, int value) {
		channel[index] = value;
	}

	public String toString() {
		sb.setLength(0);
		for (int i = 0; i < channel.length; i++) {
			int value = channel[i];

			switch (i) {
			case YAW:
				if (channel[THRO] > 10) {
					if (Math.abs(value - 50) < 20) {
						value = 50;
					} else if (value > 50) {
						value = 54;
					} else if (value <50 ){
						value = 46;
					}else {
						value = 50;
					}
				}
				break;
			case MODE:
				break;
			case THRO:

				break;

			default:
				//value = 50 + (value - 50) / 10;
			}
			
			sb.append(formatNumber(value));
		}

		String s = sb.toString();
		byte[] bytes = s.getBytes();
		int checkSum = 0;
		for (Byte b : bytes) {
			checkSum += (b.intValue() - 48);
		}

		s += formatNumber(checkSum);

		return s;
	}

	public byte[] toBytes() {
		return (toString() + ";").getBytes();
	}

	public int getValue(int channelId) {
		return channel[channelId];
	}

	private String formatNumber(Integer num) {
		if (num < 10) {
			return "00" + num.toString();
		}
		if (num < 100) {
			return "0" + num.toString();
		}

		return num.toString();

	}

}

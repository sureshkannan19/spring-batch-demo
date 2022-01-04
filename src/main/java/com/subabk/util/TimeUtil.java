package com.subabk.util;

import java.time.Clock;
import java.util.Date;

public class TimeUtil {

	public static Date getCurrentDate() {
		return Date.from(Clock.systemDefaultZone().instant());
	}
}

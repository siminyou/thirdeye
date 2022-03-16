/*
 * Copyright (c) 2022 StarTree Inc. All rights reserved.
 * Confidential and Proprietary Information of StarTree Inc.
 */

package ai.startree.thirdeye.util;

import com.google.common.annotations.VisibleForTesting;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class TimeUtils {

  /**
   * See https://stackoverflow.com/questions/8933158/how-do-i-round-a-datetime-to-the-nearest-period
   * Floors correctly only if 1 Time unit is used in the Period.
   */
  public static DateTime floorByPeriod(DateTime dt, Period period) {
    if (period.getYears() != 0) {
      return dt.yearOfEra().roundFloorCopy().minusYears(dt.getYearOfEra() % period.getYears());
    } else if (period.getMonths() != 0) {
      return dt.monthOfYear()
          .roundFloorCopy()
          .minusMonths((dt.getMonthOfYear() - 1) % period.getMonths());
    } else if (period.getWeeks() != 0) {
      return dt.weekOfWeekyear()
          .roundFloorCopy()
          .minusWeeks((dt.getWeekOfWeekyear() - 1) % period.getWeeks());
    } else if (period.getDays() != 0) {
      return dt.dayOfMonth()
          .roundFloorCopy()
          .minusDays((dt.getDayOfMonth() - 1) % period.getDays());
    } else if (period.getHours() != 0) {
      return dt.hourOfDay().roundFloorCopy().minusHours(dt.getHourOfDay() % period.getHours());
    } else if (period.getMinutes() != 0) {
      return dt.minuteOfHour()
          .roundFloorCopy()
          .minusMinutes(dt.getMinuteOfHour() % period.getMinutes());
    } else if (period.getSeconds() != 0) {
      return dt.secondOfMinute()
          .roundFloorCopy()
          .minusSeconds(dt.getSecondOfMinute() % period.getSeconds());
    }
    return dt.millisOfSecond()
        .roundCeilingCopy()
        .minusMillis(dt.getMillisOfSecond() % period.getMillis());
  }

  /**
   * Returns the smallest datetime that is of the given period, and that is >=minTimeConstraint.
   * eg: minTimeConstraint is >= Thursday 3 am - period is 1 DAY --> returns Friday.
   * eg: inclusion: minTimeConstraint is >= Thursday 0 am - period is 1 DAY --> returns Thursday.
   */
  @VisibleForTesting
  public static DateTime getSmallestDatetime(DateTime minTimeConstraint, Period timePeriod) {
    DateTime dateTimeFloored = TimeUtils.floorByPeriod(minTimeConstraint, timePeriod.toPeriod());
    if (minTimeConstraint.equals(dateTimeFloored)) {
      return dateTimeFloored;
    }
    // dateTimeConstraint bigger than floored --> add 1 period
    return dateTimeFloored.plus(timePeriod);
  }

  /**
   * Returns the biggest datetime that is of the given period, and that is <maxTimeConstraint.
   * eg: maxTimeConstraint is < Monday 4 am - period is 1 DAY --> returns Monday.
   * eg: exclusion: maxTimeConstraint is < Monday 0 am - period is 1 DAY --> returns Sunday.
   */
  @VisibleForTesting
  public static DateTime getBiggestDatetime(DateTime maxTimeConstraint, Period timePeriod) {
    DateTime dateTimeFloored = TimeUtils.floorByPeriod(maxTimeConstraint, timePeriod.toPeriod());

    if (maxTimeConstraint.equals(dateTimeFloored)) {
      // dateTimeConstraint equals floor --> should be excluded
      return dateTimeFloored.minus(timePeriod);
    }
    return dateTimeFloored;
  }
}
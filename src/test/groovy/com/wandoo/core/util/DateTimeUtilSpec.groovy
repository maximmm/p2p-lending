package com.wandoo.core.util

import spock.lang.Specification
import spock.lang.Unroll

import static com.wandoo.core.util.DateTimeUtil.toDate
import static com.wandoo.core.util.DateTimeUtil.toLocalDate
import static java.time.LocalDate.of
import static java.util.Calendar.JULY

class DateTimeUtilSpec extends Specification {

    def "Should correctly parse LocalDate to Date"() {
        expect:
        toDate(of(1990, 7, 17)) == getJavaDate(1990, JULY, 17)
    }

    def "Should correctly parse Date to LocalDate"() {
        expect:
        toLocalDate(getJavaDate(1990, JULY, 17)) == of(1990, 7, 17)
    }

    @Unroll
    def "Should throw IllegalArgumentException when method '#methodName' is provided with 'null'"() {
        when:
        DateTimeUtil."${methodName}"(null)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Date to convert can not be null."

        where:
        methodName << ["toDate", "toLocalDate"]
    }

    def getJavaDate(int year, int month, int day) {
        new GregorianCalendar(year, month, day).getTime()
    }

}

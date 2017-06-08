package io.smartcat.ranger.core.parser

import io.smartcat.ranger.core.InvalidRangeBoundsException
import io.smartcat.ranger.core.parser.DataGenerator.Builder
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll

class DataGeneratorSpec extends Specification {

    def "should throw InvalidReferenceNameException when reference does not point to any existing value"() {
        given:
        def config = '''
values:
  name: $firstName
output: $name
'''
        when:
        def dataGenerator = buildGenerator(config)

        then:
        Exception e = thrown()
        e.cause instanceof InvalidReferenceNameException
    }

    def "should use value from closest visible context for given reference"() {
        given:
        def config = '''
values:
  a:
    b: 10
    c:
      b: 11
      c:
        b: 12
        c:
          x: $b
  y: $a.c.c.c.x
output: $y
'''
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == 12
    }

    def "should be possible to define reference before vlaue"() {
        given:
        def config = '''
values:
  a: $b
  b: 5
output: $a
'''
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == 5
    }

    @Unroll
    def "should parse primitive string value when string is #text"() {
        given:
        def config = """
values:
  name: $value
output: \$name
"""
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == parsedValue

        where:
        value        | parsedValue | text
        "'value 1'"  | "value 1"   | "single quoted"
        '"value 1"'  | "value 1"   | "double quoted"
        "value 1"    | "value 1"   | "naked"
        "'value 1 '" | "value 1 "  | "single quoted with trailing space"
        '"value 1 "' | "value 1 "  | "double quoted with trailing space"
        "value 1 "   | "value 1"   | "naked with trailing space"
        "' value 1'" | " value 1"  | "single quoted with leading space"
        '" value 1"' | " value 1"  | "double quoted with leading space"
        " value 1"   | "value 1"   | "naked with leading space"
    }

    @Unroll
    def "should parse primitive integer #value"() {
        given:
        def config = """
values:
  age: $value
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == parsedValue

        where:
        value    | parsedValue
        "21465"  | 21465
        "+84982" | 84982
        "-72310" | -72310
    }

    @Unroll
    def "should parse primitive long #value"() {
        given:
        def config = """
values:
  age: $value
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == parsedValue

        where:
        value          | parsedValue
        "21474836590"  | 21474836590
        "+21474836590" | 21474836590
        "-21474836590" | -21474836590
    }

    @Unroll
    def "should parse primitive double #value"() {
        given:
        def config = """
values:
  age: $value
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == parsedValue

        where:
        value         | parsedValue
        ".12345"      | 0.12345
        ".6789e9"     | 0.6789E9
        ".6789e+3"    | 0.6789E3
        ".6789e-4"    | 0.6789E-4
        ".6789E7"     | 0.6789E7
        ".6789E+2"    | 0.6789E2
        ".6789E-5"    | 0.6789E-5
       "+.12345"      | 0.12345
        "+.6789e6"    | 0.6789E6
        "+.6789e+4"   | 0.6789E4
        "+.6789e-3"   | 0.6789E-3
        "+.6789E5"    | 0.6789E5
        "+.6789E+2"   | 0.6789E2
        "+.6789E-7"   | 0.6789E-7
        "-.12345"     | -0.12345
        "-.6789e4"    | -0.6789E4
        "-.6789e+2"   | -0.6789E2
        "-.6789e-3"   | -0.6789E-3
        "-.6789E3"    | -0.6789E3
        "-.6789E+5"   | -0.6789E5
        "-.6789E-8"   | -0.6789E-8
        "54.6789"     | 54.6789
        "54.6789e5"   | 54.6789E5
        "54.6789e+6"  | 54.6789E6
        "54.6789e-7"  | 54.6789E-7
        "54.6789E8"   | 54.6789E8
        "54.6789E+2"  | 54.6789E2
        "54.6789E-5"  | 54.6789E-5
        "+54.6789"    | 54.6789
        "+54.6789e3"  | 54.6789E3
        "+54.6789e+6" | 54.6789E6
        "+54.6789e-8" | 54.6789E-8
        "+54.6789E5"  | 54.6789E5
        "+54.6789E+3" | 54.6789E3
        "+54.6789E-8" | 54.6789E-8
        "-54.6789"    | -54.6789
        "-54.6789e4"  | -54.6789E4
        "-54.6789e+5" | -54.6789E+5
        "-54.6789e-7" | -54.6789E-7
        "-54.6789E2"  | -54.6789E2
        "-54.6789E+3" | -54.6789E3
        "-54.6789E-6" | -54.6789E-6
    }

    def "should parse null value"() {
        given:
        def config = """
values:
  age: null()
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == null
    }

    @Unroll
    def "should parse long range #expression value"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        start <= result && result <= end

        where:
        expression      | start | end
        "-10..-5"       | -10   | -5
        " -10..-5"      | -10   | -5
        "   -10..-5   " | -10   | -5
        "-10..0  "      | -10   | 0
        "  -10..5"      | -10   | 5
        "0..10"         | 0     | 10
        "5..10  "       | 5     | 10
    }

    @Unroll
    def "should throw InvalidRangeBoundsException for range #start .. #end"() {
        given:
        def config = """
values:
  age: random($start..$end)
output: \$age
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        Exception e = thrown()
        e.cause instanceof InvalidRangeBoundsException

        where:
        start | end
        -10   | -11
        -10   | -10
        0     | 0
        5     | 2
    }

    @Unroll
    def "should parse double range #expression value"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        startNum <= result && result <= endNum

        where:
        expression              | startNum | endNum
        "-10.332..-.023E-10"    | -10.332  | -0.023E-10
        "  -10.332..-.023E-10"  | -10.332  | -0.023E-10
        "-10.332..-.023E-10   " | -10.332  | -0.023E-10
        "  -10.332..-.023E-10"  | -10.332  | -0.023E-10
        "-5.2E-10..0"           | -5.2E-10 | 0d
        "10..10.5"              | 10d      | 10.5
        ".23..2.12e3"           | 0.23     | 2.12E3
    }

    @Unroll
    def "should parse discrete values #expression"() {
        given:
        def config = """
values:
  value: random([$expression])
output: \$value
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result in values

        where:
        expression                 | values
        "  5,6, 7 ,8 , 9   ,  10"  | [5L, 6L, 7L, 8L, 9L, 10L]
        "5.0, 3.4 , +.12, 0.23   " | [5d, 3.4d, 0.12d, 0.23d]
        """"a", 'b' , 'c' ,"d" """ | ["a", "b", "c", "d"]
    }

    @Unroll
    def "should parse circular value #expression"() {
        given:
        def config = """
values:
  value: circular([$expression])
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def result = []

        when:
        values.size().times { result << dataGenerator.next() }

        then:
        result == values

        where:
        expression                 | values
        "  5,6, 7 ,8 , 9   ,  10"  | [5L, 6L, 7L, 8L, 9L, 10L]
        "5.0, 3.4 , +.12, 0.23   " | [5d, 3.4d, 0.12d, 0.23d]
        """"a", 'b' , 'c' ,"d" """ | ["a", "b", "c", "d"]
    }

    @Unroll
    def "should parse string transformer #expression"() {
        given:
        def config = """
values:
output: string($expression)
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result == value

        where:
        expression                                  | value
        "' some Text '"                             | " some Text "
        '   " some Text " '                         | " some Text "
        "' some {} text {}', 'a', 123"              | " some a text 123"
        ' " some{}text {} {}{}", 3.4, 12, "x", "y"' | " some3.4text 12 xy"
    }

    @Unroll
    def "should parse json transformer #expression"() {
        given:
        def config = """
values:
  a:
    w: true
    x: 10
    y: 23.44
    z: "text"
output: json($expression)
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result == value

        where:
        expression | value
        '$a'       | '{"w":true,"x":10,"y":23.44,"z":"text"}'
        ' $a'      | '{"w":true,"x":10,"y":23.44,"z":"text"}'
        '$a '      | '{"w":true,"x":10,"y":23.44,"z":"text"}'
        ' $a '     | '{"w":true,"x":10,"y":23.44,"z":"text"}'
        '   $a  '  | '{"w":true,"x":10,"y":23.44,"z":"text"}'
    }

    @Unroll
    def "should parse time transformer #expression"() {
        given:
        def config = """
values:
  time: 1496815200000
output: time($expression)
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result == value

        where:
        expression                 | value
        '"YYYY-MM-dd", $time'      | "2017-06-07"
        '\'YYYY-MM-dd\',$time'     | "2017-06-07"
        '  "YYYY"  ,   $time'      | "2017"
        '"MM-dd",$time'            | "06-07"
        '   \'dd.MM.YYYY.\',$time' | "07.06.2017."
        '"YYYY-MM-dd", $time'      | "2017-06-07"
        '   "MM/dd/YYYY",$time  '  | "06/07/2017"
    }

    def "should parse nested expression"() {
        given:
        def config = '''
values:
  base:
    a: random([ 5,  random(10..15),20])
    b: time("YYYY-MM-dd", 1496815200000)
    c: constant string
  result:
    x: $base.a
    y: $base.b
    z: $base.c
    w: 25
    q: random([null(), ""])

output: $result
'''
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result.x in [5L, 10L, 11L, 12L, 13L, 14L, 15L, 20L]
        result.y == "2017-06-07"
        result.z == "constant string"
        result.w == 25
        result.q in [null, ""]
    }

    def "should parse config when there are no values and output is primitive"() {
        given:
        def config = """
values:
output: 3
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result  == 3
    }

    def buildGenerator(config) {
        def root = YamlUtils.load(config)
        new DataGenerator.Builder(root).build()
    }
}

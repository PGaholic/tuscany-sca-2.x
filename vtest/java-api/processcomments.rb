=begin

Scans java test source files, recursively from the current directory,
and produces a list of test metods sorted by specification line number

Assumes a test comment convention like this:

    /**
     * Lines 410,411,412,413:
     * <p>
     * A method of a conversational interface may be marked with an
     * "@EndsConversation" annotation. Once a method marked with
     * "@EndsConversation" has been called, the conversation between client and
     * service provider is at an end, which implies no further methods may be
     * called on that service within the *same* conversation.
     */
    @Test
    public void atEndsConversation1() throws Exception {


Example command line usage
>ruby processcomments.rb 

Some TODO's
Add logic to process number ranges such as "Lines 523-556" or "Lines 523 to 556"

=end
class TestMethod
  
  attr_accessor :lines_tested, :first_line_tested
  
  def initialize(text, parent)
    @text = text
    @parent = parent
    @lines_tested = init_lines_tested
    @first_line_tested = @lines_tested.first
  end  
  
  def <=>(test_method)  
    @first_line_tested.<=>(test_method.first_line_tested)  
  end  
  
  def name
    regex = /void\s*\S*\(\) /
    str = @text[regex]
    str.sub(/void\s*/, '')
  end
  
  def init_lines_tested
    lines_regex = /Line.*?$/
    lines_array = @text.scan(lines_regex)
    nums_regex = /\d{1,4}/
    line_numbers = Array.new
    lines_array.each do |line_text|
      number_strings =line_text.scan(nums_regex)
      number_strings.each {|num_string| line_numbers<<num_string.to_i}
    end
    line_numbers
  end
  
  def lines_tested_string
    lts = String.new
    lines_tested.each {|n| lts = lts +"," + n.to_s }
    lts[1..lts.length]
  end
  
  def package_name
    @parent.package_name
  end
  
  def long_name
    self.package_name + "." + self.name
  end
  
  def to_s
    long_name  + "\n" + "Lines tested: " + lines_tested_string + "\n\n"
  end
end

class TestCase
  attr_accessor :text, :test_methods
  
  def initialize(text)
    @text = text
    @test_methods = Array.new
    create_test_methods
  end  
  
  def create_test_methods
    regex = /\/\*\*\W*?$\W*?Line.*?\{/m
    test_method_text_array = text.scan(regex)
    test_method_text_array.each do |t| 
      @test_methods<<TestMethod.new(t, self)
      $num_test_methods +=1
    end
  end
  
  def package_name
    line = @text[/pack.*$/]
    line.sub!(/package /, '')
    line.sub(/;/, '')
  end
  
  def testcase_name
    text[/\S*TestCase/]
  end
end

  
def process_file(fn)
  text = String.new
  File.open(fn) { |f|  text = f.read } 
  $testcases << TestCase.new(text)
end

####

$num_files_processed = $num_test_methods = 0
$testcases = Array.new

puts "Scanning test files for " + Dir.pwd
Dir["**/*TestCase.java"].each do |filename|
  process_file(filename)
   $num_files_processed += 1
end

puts "Total files processed = #{$num_files_processed}"
puts "Number of test cases = #{$testcases.length}"
puts "Number of test methods = #{$num_test_methods}"

all_test_methods = Array.new
$testcases.each do |tc| 
  tc.test_methods.each {|m| all_test_methods<<m}
end
  
all_test_methods.sort.each {|tm| puts tm}




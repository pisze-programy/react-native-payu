
Pod::Spec.new do |s|
  s.name         = "RNPayu"
  s.version      = "1.0.0"
  s.summary      = "RNPayu"
  s.description  = <<-DESC
                   RNPayu
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "dev@blaszczyk.tk" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/krbz/react-native-payu", :tag => "master" }
  s.source_files  = "RCTPayuWidget/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"

end


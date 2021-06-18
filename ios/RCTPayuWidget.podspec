
Pod::Spec.new do |s|
  s.name         = "RCTPayuWidget"
  s.version      = "1.0.0"
  s.summary      = "https://developers.payu.com"
  s.description  = <<-DESC
                   PayU iOS SDK Implementation as React-Native Library
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "dev@blaszczyk.tk" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/krbz/react-native-payu", :tag => "master", :version => '1.0.0' }
  s.source_files = "RCTPayuWidget/*.{swift,h,m}"
  s.requires_arc = true
  s.homepage     = "https://github.com/krbz/react-native-payu"

  s.dependency "React"
  s.dependency "PayULite", "0.9.2"

  s.swift_version = '5'

end


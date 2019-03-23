
Pod::Spec.new do |s|
  s.name         = "RNRfidOrca50"
  s.version      = "1.0.0"
  s.summary      = "RNRfidOrca50"
  s.description  = <<-DESC
                  RNRfidOrca50
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNRfidOrca50.git", :tag => "master" }
  s.source_files  = "RNRfidOrca50/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  
# XRoad Release Notes
# --------------------------------------------------------------------
# Date: <04-Jun-2017>
04-Jun-2017
# Version: (default value: 1.1.0)
# Supported Mule Runtime Versions: 
${project.devkitVersion}
# New Features and Functionality
(default value: Initial version) 
Added support for TLS (HTTPS) through TlsConfiguration initialization for HTTPConduit's SSLFactory.
Added configuration elements:
* trustStorePath
* trustStorePassword
* trustStoreType
# Known Issues in this release
Example: Session ID expires after 30 days. http://github.com/solita/x-road/issues/62

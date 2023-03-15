# Changelog

Versions with a forth digit are meant for development; released versions drop the last digit.

03152023 - Version 10.10.2.1

* Added: New stream alias support
  * IProScopeHandler - the interface for application scope handlers to support non-Red5 core features
  * IAliasProvider - the interface for alias providers
  * AliasProviderAdapter - the adapter class for alias providers
* Moved: IWebhookCapable methods to IProScopeHandler
* Removed: redundant interfaces from IProStream

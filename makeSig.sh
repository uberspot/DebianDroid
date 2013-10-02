#!/bin/sh
gpg --detach-sign DebianDroid.apk
sha1sum DebianDroid.apk > DebianDroid.apk.sha1

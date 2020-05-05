import { NativeModules, NativeEventEmitter, View } from 'react-native';
import React, {useEffect} from 'react';

/**
 * Composes `View`.
 *
 * - title: string
 */
const MediaNotification = ({metadata, onPlay, onPause, children}) => {

  useEffect(() => {
    const {title, description, cover} = metadata;

    const MediaNotificationManager = NativeModules.RNMediaNotificationManager;
    const mediaEmitter = new NativeEventEmitter(MediaNotificationManager);

    const subscriptionOnPlay = mediaEmitter.addListener(
      'RNVRemoteOnPlay',
      () => onPlay && onPlay()
    );

    const subscriptionOnPause = mediaEmitter.addListener(
      'RNVRemoteOnPause',
      () => onPause && onPause()
    );

    MediaNotificationManager.metadata(title, description, cover);

    return () => {
      subscriptionOnPlay.remove();
      subscriptionOnPause.remove();
    };
  });

    return (
      <View>{children}</View>
    );
};

module.exports = MediaNotification;

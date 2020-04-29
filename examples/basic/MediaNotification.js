import { NativeModules, View } from 'react-native';
import React from 'react';
import MediaNotificationManager from './MediaNotificationManager'


/**
 * Composes `View`.
 *
 * - title: string
 */
class MediaNotification extends React.Component {

  componentDidMount() {
    MediaNotificationManager.metadata(this.props.title, this.props.description, this.props.cover);
  }

  render() {
    return (
      <View>{this.props.children}</View>
    );
  }

}


module.exports = MediaNotification;

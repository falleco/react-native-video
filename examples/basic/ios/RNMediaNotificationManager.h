//
//  RNMediaNotificationManager.h
//  VideoPlayer
//
//  Created by Israel Crisanto on 30/04/2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

#ifndef RNMediaNotificationManager_h
#define RNMediaNotificationManager_h
#import <React/RCTBridgeModule.h>
#import <MediaPlayer/MediaPlayer.h>
#import <AVFoundation/AVFoundation.h>
#import <React/RCTEventEmitter.h>


@interface RNMediaNotificationManager : RCTEventEmitter <RCTBridgeModule>

@property (nonatomic, strong) NSString* title;
@property (nonatomic, strong) NSString* descr;
@property (nonatomic, strong) UIImage* cover;

@end


#endif /* RNMediaNotificationManager_h */

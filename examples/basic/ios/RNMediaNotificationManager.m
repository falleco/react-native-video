//
//  RNMediaNotificationManager.m
//  VideoPlayer
//
//  Created by Israel Crisanto on 30/04/2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "RNMediaNotificationManager.h"
#import <React/RCTLog.h>
#import <React/RCTConvert.h>
#include <AVFoundation/AVFoundation.h>

@import MediaPlayer;

@implementation RNMediaNotificationManager
{
}

// To export a module named CalendarManager
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(metadata:(NSString *)title description:(NSString *)description cover: (NSDictionary*) cover)
{
  RCTLogInfo(@"< Pretending to create an event title %@ desc %@ cover %@", title, description, [cover objectForKey:@"uri"]);
  self.title = title;
  self.descr = description;
  
  NSURL *imageURL = [NSURL URLWithString:[cover objectForKey:@"uri"]];
  NSData *imageData = [NSData dataWithContentsOfURL:imageURL];
  self.cover = [UIImage imageWithData:imageData];

  RCTLogInfo(@"> Pretending to create an event title %@ desc %@", self.title, self.descr);
}

- (instancetype)init
{
  self = [super init];
  RCTLogInfo(@"CREATING SHIT");
  if (self) {
    [[NSNotificationCenter defaultCenter] addObserver:self
    selector:@selector(receivedBackgroundEnterNotification:)
        name:@"RNVBackgroundEnter"
      object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
    selector:@selector(receivedBackgroundLeaveNotification:)
        name:@"RNVBackgroundLeave"
      object:nil];
  }
  return self;
}

- (void)receivedBackgroundEnterNotification:(NSNotification *) notification {
  RCTLogInfo(@"Entering background title %@ desc %@", self.title, self.descr);
  
  [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
  
  // Create the audio session
  NSError *setCategoryErr = nil;
  NSError *activationErr  = nil;
  [[AVAudioSession sharedInstance] setCategory: AVAudioSessionCategoryPlayback error:&setCategoryErr];
  [[AVAudioSession sharedInstance] setActive:YES error:&activationErr];
  
  // Media metasdata
  MPNowPlayingInfoCenter *center = [MPNowPlayingInfoCenter defaultCenter];
  NSMutableDictionary *mediaDict = [NSMutableDictionary dictionary];
  [mediaDict setObject:(self.descr ? self.descr : @"DEFAULT TITLE")  forKey:MPMediaItemPropertyTitle];
  [mediaDict setObject:@"Zenklub" forKey:MPMediaItemPropertyArtist];
  [mediaDict setObject:(self.title ? self.title : @"DEFAULT ALBUM") forKey:MPMediaItemPropertyAlbumTitle];
  [mediaDict setObject:[NSNumber numberWithDouble:(1.0f)] forKey:MPNowPlayingInfoPropertyPlaybackRate];
  
  if(self.cover) {
    MPMediaItemArtwork * albumArt = [[MPMediaItemArtwork alloc] initWithImage:self.cover];
    [mediaDict setObject:albumArt forKey:MPMediaItemPropertyArtwork];
  }
    
  [center setNowPlayingInfo:mediaDict];
  
  // Control
  //[MPRemoteCommandCenter sharedCommandCenter].playCommand.enabled = NO;
  MPRemoteCommandCenter *remoteCommandCenter = [MPRemoteCommandCenter sharedCommandCenter];
  [remoteCommandCenter.playCommand addTarget:self action:@selector(actionPlay:)];
  [remoteCommandCenter.pauseCommand addTarget:self action:@selector(actionPause:)];
  
  RCTLogInfo(@"Done Entering background");
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[@"RNVRemoteOnPlay", @"RNVRemoteOnPause"];
}

- (MPRemoteCommandHandlerStatus)actionPlay:(MPRemoteCommand *)sender {
 RCTLogInfo(@"CLICKED PLAY");
  [self sendEventWithName:@"RNVRemoteOnPlay" body:@{}];
  return MPRemoteCommandHandlerStatusSuccess;
}

- (MPRemoteCommandHandlerStatus)actionPause:(MPRemoteCommand *)sender {
 RCTLogInfo(@"CLICKED PAUSE");
  [self sendEventWithName:@"RNVRemoteOnPause" body:@{}];
  return MPRemoteCommandHandlerStatusSuccess;
}

- (void)receivedBackgroundLeaveNotification:(NSNotification *) notification {
  RCTLogInfo(@"Leaving background");
  [[UIApplication sharedApplication] endReceivingRemoteControlEvents];
}

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

@end

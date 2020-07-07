#import "CallTrap.h"

#import <CallKit/CallKit.h>

@implementation CallTrap

-(void)pluginInitialize
{
    CXCallObserver *callObserverObj = [[CXCallObserver alloc] init];
    [callObserverObj setDelegate:self queue:nil]
    self.callObserver = callObserverObj;
    NSLog(@"Initialized CallTrap");
}

-(void)onReset
{
    self.callbackId = nil;
    self.callObserver = nil;
    NSLog(@"onReset of CallTrap called - resetting state");
}

- (void)onCall:(CDVInvokedUrlCommand*)command
{
    self.callbackId = command.callbackId;
    NSLog(@"onCall of CallTrap called - setting callback ID");
}

- (void)callObserver:(CXCallObserver *)callObserver callChanged:(CXCall *)call {
    NSString* callstatus = @"IDLE";

    if (call == nil || call.hasEnded == YES) {
        NSLog(@"CXCallState : Disconnected");
        callstatus = @"IDLE";
    }

    if (call.isOutgoing == YES && call.hasConnected == NO) {
        NSLog(@"CXCallState : Dialing");
        callstatus = @"OFFHOOK";
    }

    if (call.isOutgoing == NO  && call.hasConnected == NO && call.hasEnded == NO && call != nil) {
        NSLog(@"CXCallState : Incoming");
        callstatus = @"RINGING";
    }

    if (call.hasConnected == YES && call.hasEnded == NO) {
        NSLog(@"CXCallState : Connected");
        callstatus = @"OFFHOOK";
    }

    NSMutableDictionary *resultData = [NSMutableDictionary dictionaryWithCapacity:2];
    [resultData setObject:callstatus forKey:@"status"];
    [resultData setObject:nil forKey:@"number"];

    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultData];
    [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
}

@end
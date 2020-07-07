#import <CallKit/CallKit.h>
#import <Cordova/CDVPlugin.h>

@interface CallTrap : CDVPlugin<CXCallObserverDelegate>
{
    CXCallObserver *callObserverObj;
    NSString *callbackId;
}

@property (nonatomic, strong) CXCallObserver *callObserverObj;
@property (nonatomic, strong) NSString *callbackId;

- (void)onCall:(CDVInvokedUrlCommand*)command;

@end

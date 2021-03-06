//
//  OKSendCoinViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

typedef enum {
    OKFeeTypeSlow,
    OKFeeTypeRecommend,
    OKFeeTypeFast
}OKFeeType;


#import "OKSendCoinViewController.h"


@interface OKSendCoinViewController ()<UITextFieldDelegate>
//Top
@property (weak, nonatomic) IBOutlet UIView *shoukuanLabelBg;
@property (weak, nonatomic) IBOutlet UILabel *shoukuanLabel;
@property (weak, nonatomic) IBOutlet UITextField *addressTextField;
@property (weak, nonatomic) IBOutlet UIButton *addressbookBtn;
- (IBAction)addressbookBtnClick:(UIButton *)sender;

//Mid
@property (weak, nonatomic) IBOutlet UIView *amountBg;
@property (weak, nonatomic) IBOutlet UILabel *amountLabel;
@property (weak, nonatomic) IBOutlet UITextField *amountTextField;
@property (weak, nonatomic) IBOutlet UIButton *moreBtn;
- (IBAction)moreBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *balanceTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UILabel *coinTypeLabel;
@property (weak, nonatomic) IBOutlet UIButton *coinTypeBtn;
- (IBAction)coinTypeBtnClick:(UIButton *)sender;
//Bottom
@property (weak, nonatomic) IBOutlet UIView *feeLabelBg;
@property (weak, nonatomic) IBOutlet UILabel *feeLabel;
@property (weak, nonatomic) IBOutlet UILabel *feeTipsLabel;
@property (weak, nonatomic) IBOutlet UIButton *customBtn;
- (IBAction)customBtnClick:(UIButton *)sender;


@property (weak, nonatomic) IBOutlet UIView *feeTypeBgView;

@property (weak, nonatomic) IBOutlet UIView *slowBg;
@property (weak, nonatomic) IBOutlet UIView *recommendedBg;
@property (weak, nonatomic) IBOutlet UIView *fastBg;

@property (weak, nonatomic) IBOutlet OKButton *sendBtn;
- (IBAction)sendBtnClick:(OKButton *)sender;

@property (weak, nonatomic) IBOutlet UIView *slowBottomLabelBg;
@property (weak, nonatomic) IBOutlet UIView *recommendBottomLabelBg;
@property (weak, nonatomic) IBOutlet UIView *fastBottomLabelBg;

//手势
- (IBAction)tapSlowBgClick:(UITapGestureRecognizer *)sender;
- (IBAction)tapRecommendBgClick:(UITapGestureRecognizer *)sender;
- (IBAction)tapFastBgClick:(UITapGestureRecognizer *)sender;

//feeType内部控件
@property (weak, nonatomic) IBOutlet UILabel *slowTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *slowCoinAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *slowMoneyAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *slowTimeLabel;
@property (weak, nonatomic) IBOutlet UIButton *slowSelectBtn;

@property (weak, nonatomic) IBOutlet UILabel *recommendTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *recommendCoinAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *recommendMoneyAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *recommendTimeLabel;
@property (weak, nonatomic) IBOutlet UIButton *recommendSelectBtn;

@property (weak, nonatomic) IBOutlet UILabel *fastTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *fastCoinAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *fastMoneyAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *fastTimeLabel;
@property (weak, nonatomic) IBOutlet UIButton *fastSelectBtn;

@property (nonatomic,copy)NSString* currentFee_status;

@property (nonatomic,assign)OKFeeType currentFeeType;

@property (nonatomic,strong)NSDictionary *lowFeeDict;

@property (nonatomic,strong)NSDictionary *recommendFeeDict;

@property (nonatomic,strong)NSDictionary *fastFeeDict;

@property (nonatomic,copy)NSString *currentMemo;

@end

@implementation OKSendCoinViewController

+ (instancetype)sendCoinViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSendCoinViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self setNavigationBarBackgroundColorWithClearColor];
    self.title = MyLocalizedString(@"transfer", nil);
    [self stupUI];
    [self changeFeeType:OKFeeTypeRecommend];
    NSString *default_fee_status =  [kPyCommandsManager callInterface:kInterfaceGet_default_fee_status parameter:@{}];
    NSArray *default_fee_statusArray = [default_fee_status componentsSeparatedByString:@" "];
    NSString* default_fee_statusNum = [default_fee_statusArray firstObject];
    self.currentFee_status = default_fee_statusNum;
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(refreshBalance:) name:kNotiUpdate_status object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textChange:) name:UITextFieldTextDidChangeNotification object:nil];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}
- (void)stupUI
{
    [self.shoukuanLabelBg setLayerRadius:12];
    [self.amountBg setLayerRadius:12];
    [self.feeLabelBg setLayerRadius:12];
    [self.moreBtn setLayerRadius:8];
    [self.moreBtn setBackgroundColor:RGBA(196, 196, 196, 0.2)];
    [self.feeTypeBgView setLayerBoarderColor:HexColor(0xE5E5E5) width:1 radius:20];
    [self.slowBottomLabelBg setLayerRadius:20];
    [self.recommendBottomLabelBg setLayerRadius:20];
    [self.fastBottomLabelBg setLayerRadius:20];
    [self.sendBtn setLayerDefaultRadius];

    self.slowTitleLabel.text = MyLocalizedString(@"慢", nil);
    self.slowCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",kWalletManager.currentBitcoinUnit];
    self.slowTimeLabel.text = MyLocalizedString(@"约0分钟", nil);
    
    self.recommendTitleLabel.text = MyLocalizedString(@"推荐", nil);
    self.recommendCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",kWalletManager.currentBitcoinUnit];
    self.recommendTimeLabel.text = MyLocalizedString(@"约0分钟", nil);
    
    self.fastTitleLabel.text = MyLocalizedString(@"快", nil);
    self.fastCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",kWalletManager.currentBitcoinUnit];
    self.fastTimeLabel.text = MyLocalizedString(@"约0分钟", nil);
    [self.coinTypeBtn setTitle:kWalletManager.currentBitcoinUnit forState:UIControlStateNormal];
    
    //self.coinTypeBtn.hidden  = YES;
}

- (void)refreshBalance:(NSNotification *)noti
{
    NSDictionary *dict = noti.object;
    dispatch_async(dispatch_get_main_queue(), ^{
       // UI更新代码
        self.balanceLabel.text =  [dict safeStringForKey:@"balance"];
        self.coinTypeLabel.text = kWalletManager.currentBitcoinUnit;
    });
}

- (void)refreshFeeSelect
{
    self.slowTitleLabel.text = MyLocalizedString(@"慢", nil);
    self.slowCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",[self.lowFeeDict safeStringForKey:@"fee"],kWalletManager.currentBitcoinUnit];
    self.slowTimeLabel.text = [NSString stringWithFormat:@"约%@分钟",[self.lowFeeDict safeStringForKey:@"time"]];
    self.recommendTitleLabel.text = MyLocalizedString(@"推荐", nil);
    self.recommendCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",[self.recommendFeeDict safeStringForKey:@"fee"],kWalletManager.currentBitcoinUnit];
    self.recommendTimeLabel.text = [NSString stringWithFormat:@"约%@分钟",[self.recommendFeeDict safeStringForKey:@"time"]];
    
    self.fastTitleLabel.text = MyLocalizedString(@"快", nil);
    self.fastCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",[self.fastFeeDict safeStringForKey:@"fee"],kWalletManager.currentBitcoinUnit];
    self.fastTimeLabel.text = [NSString stringWithFormat:@"约%@分钟",[self.fastFeeDict safeStringForKey:@"time"]];
}

- (IBAction)addressbookBtnClick:(UIButton *)sender {
    [kTools tipMessage:@"点击了通讯录"];
}
- (IBAction)moreBtnClick:(UIButton *)sender {
    self.amountTextField.text = self.balanceLabel.text;
}
- (IBAction)coinTypeBtnClick:(UIButton *)sender {
    NSLog(@"点击了币种类型切换");
}
- (IBAction)customBtnClick:(UIButton *)sender {
    [kTools tipMessage:@"点击了自定义"];
}
- (IBAction)sendBtnClick:(OKButton *)sender {
    
    if (self.addressTextField.text.length == 0) {
        [kTools tipMessage:@"请输入转账地址"];
        return;
    }

    if (self.amountTextField.text.length == 0) {
        [kTools tipMessage:@"请输入转账金额"];
        return;
    }
    
    if ([self.balanceLabel.text doubleValue] < [self.amountTextField.text doubleValue]) {
        [kTools tipMessage:@"余额不足"];
        return;
    }
    
    
    NSDictionary *dict = [NSDictionary dictionary];
    switch (_currentFeeType) {
        case OKFeeTypeSlow:
        {
            dict = self.lowFeeDict;
        }
            break;
        case OKFeeTypeRecommend:
        {
            dict = self.recommendFeeDict;
        }
            break;
        case OKFeeTypeFast:
        {
            dict = self.fastFeeDict;
        }
            break;
        default:
            break;
    }
    OKWeakSelf(self)
    [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
        NSString *feerateTx = [dict safeStringForKey:@"tx"];
        NSDictionary *dict1 =  [kPyCommandsManager callInterface:kInterfaceMktx parameter:@{@"tx":feerateTx}];
        NSString *unSignStr = [dict1 safeStringForKey:@"tx"];
        NSString *tx = unSignStr;
        NSString *password = pwd;
        NSDictionary *signTxDict =  [kPyCommandsManager callInterface:kInterfaceSign_tx parameter:@{@"tx":tx,@"password":password}];
        NSString *signTx = [signTxDict safeStringForKey:@"tx"];
        [kPyCommandsManager callInterface:kInterfaceBroadcast_tx parameter:@{@"tx":signTx}];
        [kTools tipMessage:@"发送成功"];
        [weakself.navigationController popViewControllerAnimated:YES];
    }];
}



- (void)loadFee
{
    [self loadZeroFee];
    [self loadReRecommendFee];
    [self loadFastFee];
}
- (void)loadFastFee
{
    NSString *status = [NSString stringWithFormat:@"%zd",[self.currentFee_status integerValue] * 2];
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{self.addressTextField.text:self.amountTextField.text};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":status}];
    self.fastFeeDict = dict;
}
- (void)loadReRecommendFee
{
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{self.addressTextField.text:self.amountTextField.text};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":self.currentFee_status}];
    self.recommendFeeDict = dict;
}
- (void)loadZeroFee
{
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{self.addressTextField.text:self.amountTextField.text};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":@"0"}];
    self.lowFeeDict = dict;
}


- (IBAction)tapSlowBgClick:(UITapGestureRecognizer *)sender {
    if (self.currentFeeType != OKFeeTypeSlow) {
        [self changeFeeType:OKFeeTypeSlow];
    }
}

- (IBAction)tapRecommendBgClick:(UITapGestureRecognizer *)sender
{
    if (self.currentFeeType != OKFeeTypeRecommend) {
        [self changeFeeType:OKFeeTypeRecommend];
    }
}
- (IBAction)tapFastBgClick:(UITapGestureRecognizer *)sender
{
    if (self.currentFeeType != OKFeeTypeFast) {
        [self changeFeeType:OKFeeTypeFast];
    }
}

#pragma mark - OKFeeType
- (void)changeFeeType:(OKFeeType)feeType
{
    _currentFeeType = feeType;
    switch (_currentFeeType) {
        case OKFeeTypeSlow:
        {
            self.slowSelectBtn.hidden = NO;
            self.recommendSelectBtn.hidden = YES;
            self.fastSelectBtn.hidden = YES;
            [self.slowBg shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.recommendedBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.fastBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
        }
            break;
        case OKFeeTypeRecommend:
        {
            self.slowSelectBtn.hidden = YES;
            self.recommendSelectBtn.hidden = NO;
            self.fastSelectBtn.hidden = YES;
            [self.slowBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.recommendedBg shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.fastBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
        }
            break;
        case OKFeeTypeFast:
        {
            self.slowSelectBtn.hidden = YES;
            self.recommendSelectBtn.hidden = YES;
            self.fastSelectBtn.hidden = NO;
            [self.slowBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.recommendedBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.fastBg shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
        }
            break;
        default:
            break;
    }
}


#pragma mark - UITextFieldDelegate
- (void)textChange:(NSString *)str
{
    if (self.addressTextField.text.length > 0 && self.amountTextField.text.length > 0 && [self.amountTextField.text doubleValue] > 0 && [self.amountTextField.text doubleValue] <= [self.balanceLabel.text doubleValue]) {
        [self loadFee];
        [self refreshFeeSelect];
    }
}
@end

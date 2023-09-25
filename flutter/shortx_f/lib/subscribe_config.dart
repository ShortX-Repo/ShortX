class SubscriptionConfig {
  final double priceUSD;
  final double priceCNY;
  final String email;
  final String qq;
  final String extra1;

  const SubscriptionConfig({
    required this.priceUSD,
    required this.priceCNY,
    required this.email,
    required this.qq,
    required this.extra1,
  });

  factory SubscriptionConfig.fromJson(Map<String, dynamic> json) {
    return SubscriptionConfig(
      priceUSD: json['priceUSD'],
      priceCNY: json['priceCNY'],
      email: json['email'],
      qq: json['qq'],
      extra1: json['extra1'],
    );
  }
}

extension SubscriptionConfigExt on SubscriptionConfig {
  String formatSubscribeInfo() {
    return """
## 激活码购买

有效期自购买日起算10年，一个激活码同一时刻仅能激活一台设备，支持换设备使用。
1. 支付 **${priceCNY}** CNY  ( **${priceUSD}** USD)
2. 发邮件至 **${email}**
3. 邮件写 **ShortX高级版本申请**
4. 加上带**支付订单号**的支付截图附件
5. 发送邮件

> 💡 我们会尽快回复高级版激活码，一般不会超过12小时，如果超时未收到请检查下垃圾箱，或者联系Q: ${qq}
> 发邮件时可以使用QQ、163、gmail等邮箱，不要使用手机彩信、outlook邮箱。

---------------
---------------
---------------
---------------

## 或者去Google Play购买

 **ShortX** 已在Google Play发布，你可以前往购买下载；如果你已购买，可以通过下方验证按钮验证你的资格。
""";
  }
}

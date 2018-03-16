$nf = netflix
$ggl = google
$amzn = amazon
$ali = alibaba
$stm = steam

ping –n 14 www.$ggl.com >> $ggl-$(get-date -f MMddyyyy.hhmmss).ping
ping –n 14 www.$nf.com >> $nf-$(get-date -f MMddyyyy.hhmmss).ping
ping –n 14 www.$amzn.com >> $amzn-$(get-date -f MMddyyyy.hhmmss).ping
ping –n 14 www.$ali.com >> $ali-$(get-date -f MMddyyyy.hhmmss).ping
ping –n 14 www.$stm.com >> $stm-$(get-date -f MMddyyyy.hhmmss).ping

tracert www.$ggl.com >> $ggl-$(get-date -f MMddyyyy.hhmmss).rt
tracert www.$nf.com >> $nf-$(get-date -f MMddyyyy.hhmmss).rt
tracert www.$amzn.com >> $amzn-$(get-date -f MMddyyyy.hhmmss).rt
tracert www.$ali.com >> $ali-$(get-date -f MMddyyyy.hhmmss).rt
tracert www.$stm.com >> $stm-$(get-date -f MMddyyyy.hhmmss).rt
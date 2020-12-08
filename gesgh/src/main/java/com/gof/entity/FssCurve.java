package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.enums.EBaseMatCd;
import com.gof.interfaces.EntityIdentifier;


public class FssCurve implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = 4104586581897784001L;
	
	private String sceId;
	private Double m1;
	private Double m2;
	private Double m3;
	private Double m4;
	private Double m5;
	private Double m6;
	private Double m7;
	private Double m8;
	private Double m9;
	private Double m10;
	private Double m11;
	private Double m12;
	private Double m13;
	private Double m14;
	private Double m15;
	private Double m16;
	private Double m17;
	private Double m18;
	private Double m19;
	private Double m20;
	private Double m21;
	private Double m22;
	private Double m23;
	private Double m24;
	private Double m25;
	private Double m26;
	private Double m27;
	private Double m28;
	private Double m29;
	private Double m30;
	private Double m31;
	private Double m32;
	private Double m33;
	private Double m34;
	private Double m35;
	private Double m36;
	private Double m37;
	private Double m38;
	private Double m39;
	private Double m40;
	private Double m41;
	private Double m42;
	private Double m43;
	private Double m44;
	private Double m45;
	private Double m46;
	private Double m47;
	private Double m48;
	private Double m49;
	private Double m50;
	private Double m51;
	private Double m52;
	private Double m53;
	private Double m54;
	private Double m55;
	private Double m56;
	private Double m57;
	private Double m58;
	private Double m59;
	private Double m60;
	private Double m61;
	private Double m62;
	private Double m63;
	private Double m64;
	private Double m65;
	private Double m66;
	private Double m67;
	private Double m68;
	private Double m69;
	private Double m70;
	private Double m71;
	private Double m72;
	private Double m73;
	private Double m74;
	private Double m75;
	private Double m76;
	private Double m77;
	private Double m78;
	private Double m79;
	private Double m80;
	private Double m81;
	private Double m82;
	private Double m83;
	private Double m84;
	private Double m85;
	private Double m86;
	private Double m87;
	private Double m88;
	private Double m89;
	private Double m90;
	private Double m91;
	private Double m92;
	private Double m93;
	private Double m94;
	private Double m95;
	private Double m96;
	private Double m97;
	private Double m98;
	private Double m99;
	private Double m100;
	private Double m101;
	private Double m102;
	private Double m103;
	private Double m104;
	private Double m105;
	private Double m106;
	private Double m107;
	private Double m108;
	private Double m109;
	private Double m110;
	private Double m111;
	private Double m112;
	private Double m113;
	private Double m114;
	private Double m115;
	private Double m116;
	private Double m117;
	private Double m118;
	private Double m119;
	private Double m120;
	private Double m121;
	private Double m122;
	private Double m123;
	private Double m124;
	private Double m125;
	private Double m126;
	private Double m127;
	private Double m128;
	private Double m129;
	private Double m130;
	private Double m131;
	private Double m132;
	private Double m133;
	private Double m134;
	private Double m135;
	private Double m136;
	private Double m137;
	private Double m138;
	private Double m139;
	private Double m140;
	private Double m141;
	private Double m142;
	private Double m143;
	private Double m144;
	private Double m145;
	private Double m146;
	private Double m147;
	private Double m148;
	private Double m149;
	private Double m150;
	private Double m151;
	private Double m152;
	private Double m153;
	private Double m154;
	private Double m155;
	private Double m156;
	private Double m157;
	private Double m158;
	private Double m159;
	private Double m160;
	private Double m161;
	private Double m162;
	private Double m163;
	private Double m164;
	private Double m165;
	private Double m166;
	private Double m167;
	private Double m168;
	private Double m169;
	private Double m170;
	private Double m171;
	private Double m172;
	private Double m173;
	private Double m174;
	private Double m175;
	private Double m176;
	private Double m177;
	private Double m178;
	private Double m179;
	private Double m180;
	private Double m181;
	private Double m182;
	private Double m183;
	private Double m184;
	private Double m185;
	private Double m186;
	private Double m187;
	private Double m188;
	private Double m189;
	private Double m190;
	private Double m191;
	private Double m192;
	private Double m193;
	private Double m194;
	private Double m195;
	private Double m196;
	private Double m197;
	private Double m198;
	private Double m199;
	private Double m200;
	private Double m201;
	private Double m202;
	private Double m203;
	private Double m204;
	private Double m205;
	private Double m206;
	private Double m207;
	private Double m208;
	private Double m209;
	private Double m210;
	private Double m211;
	private Double m212;
	private Double m213;
	private Double m214;
	private Double m215;
	private Double m216;
	private Double m217;
	private Double m218;
	private Double m219;
	private Double m220;
	private Double m221;
	private Double m222;
	private Double m223;
	private Double m224;
	private Double m225;
	private Double m226;
	private Double m227;
	private Double m228;
	private Double m229;
	private Double m230;
	private Double m231;
	private Double m232;
	private Double m233;
	private Double m234;
	private Double m235;
	private Double m236;
	private Double m237;
	private Double m238;
	private Double m239;
	private Double m240;
	private Double m241;
	private Double m242;
	private Double m243;
	private Double m244;
	private Double m245;
	private Double m246;
	private Double m247;
	private Double m248;
	private Double m249;
	private Double m250;
	private Double m251;
	private Double m252;
	private Double m253;
	private Double m254;
	private Double m255;
	private Double m256;
	private Double m257;
	private Double m258;
	private Double m259;
	private Double m260;
	private Double m261;
	private Double m262;
	private Double m263;
	private Double m264;
	private Double m265;
	private Double m266;
	private Double m267;
	private Double m268;
	private Double m269;
	private Double m270;
	private Double m271;
	private Double m272;
	private Double m273;
	private Double m274;
	private Double m275;
	private Double m276;
	private Double m277;
	private Double m278;
	private Double m279;
	private Double m280;
	private Double m281;
	private Double m282;
	private Double m283;
	private Double m284;
	private Double m285;
	private Double m286;
	private Double m287;
	private Double m288;
	private Double m289;
	private Double m290;
	private Double m291;
	private Double m292;
	private Double m293;
	private Double m294;
	private Double m295;
	private Double m296;
	private Double m297;
	private Double m298;
	private Double m299;
	private Double m300;
	private Double m301;
	private Double m302;
	private Double m303;
	private Double m304;
	private Double m305;
	private Double m306;
	private Double m307;
	private Double m308;
	private Double m309;
	private Double m310;
	private Double m311;
	private Double m312;
	private Double m313;
	private Double m314;
	private Double m315;
	private Double m316;
	private Double m317;
	private Double m318;
	private Double m319;
	private Double m320;
	private Double m321;
	private Double m322;
	private Double m323;
	private Double m324;
	private Double m325;
	private Double m326;
	private Double m327;
	private Double m328;
	private Double m329;
	private Double m330;
	private Double m331;
	private Double m332;
	private Double m333;
	private Double m334;
	private Double m335;
	private Double m336;
	private Double m337;
	private Double m338;
	private Double m339;
	private Double m340;
	private Double m341;
	private Double m342;
	private Double m343;
	private Double m344;
	private Double m345;
	private Double m346;
	private Double m347;
	private Double m348;
	private Double m349;
	private Double m350;
	private Double m351;
	private Double m352;
	private Double m353;
	private Double m354;
	private Double m355;
	private Double m356;
	private Double m357;
	private Double m358;
	private Double m359;
	private Double m360;
	private Double m361;
	private Double m362;
	private Double m363;
	private Double m364;
	private Double m365;
	private Double m366;
	private Double m367;
	private Double m368;
	private Double m369;
	private Double m370;
	private Double m371;
	private Double m372;
	private Double m373;
	private Double m374;
	private Double m375;
	private Double m376;
	private Double m377;
	private Double m378;
	private Double m379;
	private Double m380;
	private Double m381;
	private Double m382;
	private Double m383;
	private Double m384;
	private Double m385;
	private Double m386;
	private Double m387;
	private Double m388;
	private Double m389;
	private Double m390;
	private Double m391;
	private Double m392;
	private Double m393;
	private Double m394;
	private Double m395;
	private Double m396;
	private Double m397;
	private Double m398;
	private Double m399;
	private Double m400;
	private Double m401;
	private Double m402;
	private Double m403;
	private Double m404;
	private Double m405;
	private Double m406;
	private Double m407;
	private Double m408;
	private Double m409;
	private Double m410;
	private Double m411;
	private Double m412;
	private Double m413;
	private Double m414;
	private Double m415;
	private Double m416;
	private Double m417;
	private Double m418;
	private Double m419;
	private Double m420;
	private Double m421;
	private Double m422;
	private Double m423;
	private Double m424;
	private Double m425;
	private Double m426;
	private Double m427;
	private Double m428;
	private Double m429;
	private Double m430;
	private Double m431;
	private Double m432;
	private Double m433;
	private Double m434;
	private Double m435;
	private Double m436;
	private Double m437;
	private Double m438;
	private Double m439;
	private Double m440;
	private Double m441;
	private Double m442;
	private Double m443;
	private Double m444;
	private Double m445;
	private Double m446;
	private Double m447;
	private Double m448;
	private Double m449;
	private Double m450;
	private Double m451;
	private Double m452;
	private Double m453;
	private Double m454;
	private Double m455;
	private Double m456;
	private Double m457;
	private Double m458;
	private Double m459;
	private Double m460;
	private Double m461;
	private Double m462;
	private Double m463;
	private Double m464;
	private Double m465;
	private Double m466;
	private Double m467;
	private Double m468;
	private Double m469;
	private Double m470;
	private Double m471;
	private Double m472;
	private Double m473;
	private Double m474;
	private Double m475;
	private Double m476;
	private Double m477;
	private Double m478;
	private Double m479;
	private Double m480;
	private Double m481;
	private Double m482;
	private Double m483;
	private Double m484;
	private Double m485;
	private Double m486;
	private Double m487;
	private Double m488;
	private Double m489;
	private Double m490;
	private Double m491;
	private Double m492;
	private Double m493;
	private Double m494;
	private Double m495;
	private Double m496;
	private Double m497;
	private Double m498;
	private Double m499;
	private Double m500;
	private Double m501;
	private Double m502;
	private Double m503;
	private Double m504;
	private Double m505;
	private Double m506;
	private Double m507;
	private Double m508;
	private Double m509;
	private Double m510;
	private Double m511;
	private Double m512;
	private Double m513;
	private Double m514;
	private Double m515;
	private Double m516;
	private Double m517;
	private Double m518;
	private Double m519;
	private Double m520;
	private Double m521;
	private Double m522;
	private Double m523;
	private Double m524;
	private Double m525;
	private Double m526;
	private Double m527;
	private Double m528;
	private Double m529;
	private Double m530;
	private Double m531;
	private Double m532;
	private Double m533;
	private Double m534;
	private Double m535;
	private Double m536;
	private Double m537;
	private Double m538;
	private Double m539;
	private Double m540;
	private Double m541;
	private Double m542;
	private Double m543;
	private Double m544;
	private Double m545;
	private Double m546;
	private Double m547;
	private Double m548;
	private Double m549;
	private Double m550;
	private Double m551;
	private Double m552;
	private Double m553;
	private Double m554;
	private Double m555;
	private Double m556;
	private Double m557;
	private Double m558;
	private Double m559;
	private Double m560;
	private Double m561;
	private Double m562;
	private Double m563;
	private Double m564;
	private Double m565;
	private Double m566;
	private Double m567;
	private Double m568;
	private Double m569;
	private Double m570;
	private Double m571;
	private Double m572;
	private Double m573;
	private Double m574;
	private Double m575;
	private Double m576;
	private Double m577;
	private Double m578;
	private Double m579;
	private Double m580;
	private Double m581;
	private Double m582;
	private Double m583;
	private Double m584;
	private Double m585;
	private Double m586;
	private Double m587;
	private Double m588;
	private Double m589;
	private Double m590;
	private Double m591;
	private Double m592;
	private Double m593;
	private Double m594;
	private Double m595;
	private Double m596;
	private Double m597;
	private Double m598;
	private Double m599;
	private Double m600;
	private Double m601;
	private Double m602;
	private Double m603;
	private Double m604;
	private Double m605;
	private Double m606;
	private Double m607;
	private Double m608;
	private Double m609;
	private Double m610;
	private Double m611;
	private Double m612;
	private Double m613;
	private Double m614;
	private Double m615;
	private Double m616;
	private Double m617;
	private Double m618;
	private Double m619;
	private Double m620;
	private Double m621;
	private Double m622;
	private Double m623;
	private Double m624;
	private Double m625;
	private Double m626;
	private Double m627;
	private Double m628;
	private Double m629;
	private Double m630;
	private Double m631;
	private Double m632;
	private Double m633;
	private Double m634;
	private Double m635;
	private Double m636;
	private Double m637;
	private Double m638;
	private Double m639;
	private Double m640;
	private Double m641;
	private Double m642;
	private Double m643;
	private Double m644;
	private Double m645;
	private Double m646;
	private Double m647;
	private Double m648;
	private Double m649;
	private Double m650;
	private Double m651;
	private Double m652;
	private Double m653;
	private Double m654;
	private Double m655;
	private Double m656;
	private Double m657;
	private Double m658;
	private Double m659;
	private Double m660;
	private Double m661;
	private Double m662;
	private Double m663;
	private Double m664;
	private Double m665;
	private Double m666;
	private Double m667;
	private Double m668;
	private Double m669;
	private Double m670;
	private Double m671;
	private Double m672;
	private Double m673;
	private Double m674;
	private Double m675;
	private Double m676;
	private Double m677;
	private Double m678;
	private Double m679;
	private Double m680;
	private Double m681;
	private Double m682;
	private Double m683;
	private Double m684;
	private Double m685;
	private Double m686;
	private Double m687;
	private Double m688;
	private Double m689;
	private Double m690;
	private Double m691;
	private Double m692;
	private Double m693;
	private Double m694;
	private Double m695;
	private Double m696;
	private Double m697;
	private Double m698;
	private Double m699;
	private Double m700;
	private Double m701;
	private Double m702;
	private Double m703;
	private Double m704;
	private Double m705;
	private Double m706;
	private Double m707;
	private Double m708;
	private Double m709;
	private Double m710;
	private Double m711;
	private Double m712;
	private Double m713;
	private Double m714;
	private Double m715;
	private Double m716;
	private Double m717;
	private Double m718;
	private Double m719;
	private Double m720;
	private Double m721;
	private Double m722;
	private Double m723;
	private Double m724;
	private Double m725;
	private Double m726;
	private Double m727;
	private Double m728;
	private Double m729;
	private Double m730;
	private Double m731;
	private Double m732;
	private Double m733;
	private Double m734;
	private Double m735;
	private Double m736;
	private Double m737;
	private Double m738;
	private Double m739;
	private Double m740;
	private Double m741;
	private Double m742;
	private Double m743;
	private Double m744;
	private Double m745;
	private Double m746;
	private Double m747;
	private Double m748;
	private Double m749;
	private Double m750;
	private Double m751;
	private Double m752;
	private Double m753;
	private Double m754;
	private Double m755;
	private Double m756;
	private Double m757;
	private Double m758;
	private Double m759;
	private Double m760;
	private Double m761;
	private Double m762;
	private Double m763;
	private Double m764;
	private Double m765;
	private Double m766;
	private Double m767;
	private Double m768;
	private Double m769;
	private Double m770;
	private Double m771;
	private Double m772;
	private Double m773;
	private Double m774;
	private Double m775;
	private Double m776;
	private Double m777;
	private Double m778;
	private Double m779;
	private Double m780;
	private Double m781;
	private Double m782;
	private Double m783;
	private Double m784;
	private Double m785;
	private Double m786;
	private Double m787;
	private Double m788;
	private Double m789;
	private Double m790;
	private Double m791;
	private Double m792;
	private Double m793;
	private Double m794;
	private Double m795;
	private Double m796;
	private Double m797;
	private Double m798;
	private Double m799;
	private Double m800;
	private Double m801;
	private Double m802;
	private Double m803;
	private Double m804;
	private Double m805;
	private Double m806;
	private Double m807;
	private Double m808;
	private Double m809;
	private Double m810;
	private Double m811;
	private Double m812;
	private Double m813;
	private Double m814;
	private Double m815;
	private Double m816;
	private Double m817;
	private Double m818;
	private Double m819;
	private Double m820;
	private Double m821;
	private Double m822;
	private Double m823;
	private Double m824;
	private Double m825;
	private Double m826;
	private Double m827;
	private Double m828;
	private Double m829;
	private Double m830;
	private Double m831;
	private Double m832;
	private Double m833;
	private Double m834;
	private Double m835;
	private Double m836;
	private Double m837;
	private Double m838;
	private Double m839;
	private Double m840;
	private Double m841;
	private Double m842;
	private Double m843;
	private Double m844;
	private Double m845;
	private Double m846;
	private Double m847;
	private Double m848;
	private Double m849;
	private Double m850;
	private Double m851;
	private Double m852;
	private Double m853;
	private Double m854;
	private Double m855;
	private Double m856;
	private Double m857;
	private Double m858;
	private Double m859;
	private Double m860;
	private Double m861;
	private Double m862;
	private Double m863;
	private Double m864;
	private Double m865;
	private Double m866;
	private Double m867;
	private Double m868;
	private Double m869;
	private Double m870;
	private Double m871;
	private Double m872;
	private Double m873;
	private Double m874;
	private Double m875;
	private Double m876;
	private Double m877;
	private Double m878;
	private Double m879;
	private Double m880;
	private Double m881;
	private Double m882;
	private Double m883;
	private Double m884;
	private Double m885;
	private Double m886;
	private Double m887;
	private Double m888;
	private Double m889;
	private Double m890;
	private Double m891;
	private Double m892;
	private Double m893;
	private Double m894;
	private Double m895;
	private Double m896;
	private Double m897;
	private Double m898;
	private Double m899;
	private Double m900;
	private Double m901;
	private Double m902;
	private Double m903;
	private Double m904;
	private Double m905;
	private Double m906;
	private Double m907;
	private Double m908;
	private Double m909;
	private Double m910;
	private Double m911;
	private Double m912;
	private Double m913;
	private Double m914;
	private Double m915;
	private Double m916;
	private Double m917;
	private Double m918;
	private Double m919;
	private Double m920;
	private Double m921;
	private Double m922;
	private Double m923;
	private Double m924;
	private Double m925;
	private Double m926;
	private Double m927;
	private Double m928;
	private Double m929;
	private Double m930;
	private Double m931;
	private Double m932;
	private Double m933;
	private Double m934;
	private Double m935;
	private Double m936;
	private Double m937;
	private Double m938;
	private Double m939;
	private Double m940;
	private Double m941;
	private Double m942;
	private Double m943;
	private Double m944;
	private Double m945;
	private Double m946;
	private Double m947;
	private Double m948;
	private Double m949;
	private Double m950;
	private Double m951;
	private Double m952;
	private Double m953;
	private Double m954;
	private Double m955;
	private Double m956;
	private Double m957;
	private Double m958;
	private Double m959;
	private Double m960;
	private Double m961;
	private Double m962;
	private Double m963;
	private Double m964;
	private Double m965;
	private Double m966;
	private Double m967;
	private Double m968;
	private Double m969;
	private Double m970;
	private Double m971;
	private Double m972;
	private Double m973;
	private Double m974;
	private Double m975;
	private Double m976;
	private Double m977;
	private Double m978;
	private Double m979;
	private Double m980;
	private Double m981;
	private Double m982;
	private Double m983;
	private Double m984;
	private Double m985;
	private Double m986;
	private Double m987;
	private Double m988;
	private Double m989;
	private Double m990;
	private Double m991;
	private Double m992;
	private Double m993;
	private Double m994;
	private Double m995;
	private Double m996;
	private Double m997;
	private Double m998;
	private Double m999;
	private Double m1000;
	private Double m1001;
	private Double m1002;
	private Double m1003;
	private Double m1004;
	private Double m1005;
	private Double m1006;
	private Double m1007;
	private Double m1008;
	private Double m1009;
	private Double m1010;
	private Double m1011;
	private Double m1012;
	private Double m1013;
	private Double m1014;
	private Double m1015;
	private Double m1016;
	private Double m1017;
	private Double m1018;
	private Double m1019;
	private Double m1020;
	private Double m1021;
	private Double m1022;
	private Double m1023;
	private Double m1024;
	private Double m1025;
	private Double m1026;
	private Double m1027;
	private Double m1028;
	private Double m1029;
	private Double m1030;
	private Double m1031;
	private Double m1032;
	private Double m1033;
	private Double m1034;
	private Double m1035;
	private Double m1036;
	private Double m1037;
	private Double m1038;
	private Double m1039;
	private Double m1040;
	private Double m1041;
	private Double m1042;
	private Double m1043;
	private Double m1044;
	private Double m1045;
	private Double m1046;
	private Double m1047;
	private Double m1048;
	private Double m1049;
	private Double m1050;
	private Double m1051;
	private Double m1052;
	private Double m1053;
	private Double m1054;
	private Double m1055;
	private Double m1056;
	private Double m1057;
	private Double m1058;
	private Double m1059;
	private Double m1060;
	private Double m1061;
	private Double m1062;
	private Double m1063;
	private Double m1064;
	private Double m1065;
	private Double m1066;
	private Double m1067;
	private Double m1068;
	private Double m1069;
	private Double m1070;
	private Double m1071;
	private Double m1072;
	private Double m1073;
	private Double m1074;
	private Double m1075;
	private Double m1076;
	private Double m1077;
	private Double m1078;
	private Double m1079;
	private Double m1080;
	private Double m1081;
	private Double m1082;
	private Double m1083;
	private Double m1084;
	private Double m1085;
	private Double m1086;
	private Double m1087;
	private Double m1088;
	private Double m1089;
	private Double m1090;
	private Double m1091;
	private Double m1092;
	private Double m1093;
	private Double m1094;
	private Double m1095;
	private Double m1096;
	private Double m1097;
	private Double m1098;
	private Double m1099;
	private Double m1100;
	private Double m1101;
	private Double m1102;
	private Double m1103;
	private Double m1104;
	private Double m1105;
	private Double m1106;
	private Double m1107;
	private Double m1108;
	private Double m1109;
	private Double m1110;
	private Double m1111;
	private Double m1112;
	private Double m1113;
	private Double m1114;
	private Double m1115;
	private Double m1116;
	private Double m1117;
	private Double m1118;
	private Double m1119;
	private Double m1120;
	private Double m1121;
	private Double m1122;
	private Double m1123;
	private Double m1124;
	private Double m1125;
	private Double m1126;
	private Double m1127;
	private Double m1128;
	private Double m1129;
	private Double m1130;
	private Double m1131;
	private Double m1132;
	private Double m1133;
	private Double m1134;
	private Double m1135;
	private Double m1136;
	private Double m1137;
	private Double m1138;
	private Double m1139;
	private Double m1140;
	private Double m1141;
	private Double m1142;
	private Double m1143;
	private Double m1144;
	private Double m1145;
	private Double m1146;
	private Double m1147;
	private Double m1148;
	private Double m1149;
	private Double m1150;
	private Double m1151;
	private Double m1152;
	private Double m1153;
	private Double m1154;
	private Double m1155;
	private Double m1156;
	private Double m1157;
	private Double m1158;
	private Double m1159;
	private Double m1160;
	private Double m1161;
	private Double m1162;
	private Double m1163;
	private Double m1164;
	private Double m1165;
	private Double m1166;
	private Double m1167;
	private Double m1168;
	private Double m1169;
	private Double m1170;
	private Double m1171;
	private Double m1172;
	private Double m1173;
	private Double m1174;
	private Double m1175;
	private Double m1176;
	private Double m1177;
	private Double m1178;
	private Double m1179;
	private Double m1180;
	private Double m1181;
	private Double m1182;
	private Double m1183;
	private Double m1184;
	private Double m1185;
	private Double m1186;
	private Double m1187;
	private Double m1188;
	private Double m1189;
	private Double m1190;
	private Double m1191;
	private Double m1192;
	private Double m1193;
	private Double m1194;
	private Double m1195;
	private Double m1196;
	private Double m1197;
	private Double m1198;
	private Double m1199;
	private Double m1200;
	
	public FssCurve() {}

	public String getSceId() {
		return sceId;
	}

	public void setSceId(String sceId) {
		this.sceId = sceId;
	}

	public Double getM1() {
		return m1;
	}

	public void setM1(Double m1) {
		this.m1 = m1;
	}

	public Double getM2() {
		return m2;
	}

	public void setM2(Double m2) {
		this.m2 = m2;
	}

	public Double getM3() {
		return m3;
	}

	public void setM3(Double m3) {
		this.m3 = m3;
	}

	public Double getM4() {
		return m4;
	}

	public void setM4(Double m4) {
		this.m4 = m4;
	}

	public Double getM5() {
		return m5;
	}

	public void setM5(Double m5) {
		this.m5 = m5;
	}

	public Double getM6() {
		return m6;
	}

	public void setM6(Double m6) {
		this.m6 = m6;
	}

	public Double getM7() {
		return m7;
	}

	public void setM7(Double m7) {
		this.m7 = m7;
	}

	public Double getM8() {
		return m8;
	}

	public void setM8(Double m8) {
		this.m8 = m8;
	}

	public Double getM9() {
		return m9;
	}

	public void setM9(Double m9) {
		this.m9 = m9;
	}

	public Double getM10() {
		return m10;
	}

	public void setM10(Double m10) {
		this.m10 = m10;
	}

	public Double getM11() {
		return m11;
	}

	public void setM11(Double m11) {
		this.m11 = m11;
	}

	public Double getM12() {
		return m12;
	}

	public void setM12(Double m12) {
		this.m12 = m12;
	}

	public Double getM13() {
		return m13;
	}

	public void setM13(Double m13) {
		this.m13 = m13;
	}

	public Double getM14() {
		return m14;
	}

	public void setM14(Double m14) {
		this.m14 = m14;
	}

	public Double getM15() {
		return m15;
	}

	public void setM15(Double m15) {
		this.m15 = m15;
	}

	public Double getM16() {
		return m16;
	}

	public void setM16(Double m16) {
		this.m16 = m16;
	}

	public Double getM17() {
		return m17;
	}

	public void setM17(Double m17) {
		this.m17 = m17;
	}

	public Double getM18() {
		return m18;
	}

	public void setM18(Double m18) {
		this.m18 = m18;
	}

	public Double getM19() {
		return m19;
	}

	public void setM19(Double m19) {
		this.m19 = m19;
	}

	public Double getM20() {
		return m20;
	}

	public void setM20(Double m20) {
		this.m20 = m20;
	}

	public Double getM21() {
		return m21;
	}

	public void setM21(Double m21) {
		this.m21 = m21;
	}

	public Double getM22() {
		return m22;
	}

	public void setM22(Double m22) {
		this.m22 = m22;
	}

	public Double getM23() {
		return m23;
	}

	public void setM23(Double m23) {
		this.m23 = m23;
	}

	public Double getM24() {
		return m24;
	}

	public void setM24(Double m24) {
		this.m24 = m24;
	}

	public Double getM25() {
		return m25;
	}

	public void setM25(Double m25) {
		this.m25 = m25;
	}

	public Double getM26() {
		return m26;
	}

	public void setM26(Double m26) {
		this.m26 = m26;
	}

	public Double getM27() {
		return m27;
	}

	public void setM27(Double m27) {
		this.m27 = m27;
	}

	public Double getM28() {
		return m28;
	}

	public void setM28(Double m28) {
		this.m28 = m28;
	}

	public Double getM29() {
		return m29;
	}

	public void setM29(Double m29) {
		this.m29 = m29;
	}

	public Double getM30() {
		return m30;
	}

	public void setM30(Double m30) {
		this.m30 = m30;
	}

	public Double getM31() {
		return m31;
	}

	public void setM31(Double m31) {
		this.m31 = m31;
	}

	public Double getM32() {
		return m32;
	}

	public void setM32(Double m32) {
		this.m32 = m32;
	}

	public Double getM33() {
		return m33;
	}

	public void setM33(Double m33) {
		this.m33 = m33;
	}

	public Double getM34() {
		return m34;
	}

	public void setM34(Double m34) {
		this.m34 = m34;
	}

	public Double getM35() {
		return m35;
	}

	public void setM35(Double m35) {
		this.m35 = m35;
	}

	public Double getM36() {
		return m36;
	}

	public void setM36(Double m36) {
		this.m36 = m36;
	}

	public Double getM37() {
		return m37;
	}

	public void setM37(Double m37) {
		this.m37 = m37;
	}

	public Double getM38() {
		return m38;
	}

	public void setM38(Double m38) {
		this.m38 = m38;
	}

	public Double getM39() {
		return m39;
	}

	public void setM39(Double m39) {
		this.m39 = m39;
	}

	public Double getM40() {
		return m40;
	}

	public void setM40(Double m40) {
		this.m40 = m40;
	}

	public Double getM41() {
		return m41;
	}

	public void setM41(Double m41) {
		this.m41 = m41;
	}

	public Double getM42() {
		return m42;
	}

	public void setM42(Double m42) {
		this.m42 = m42;
	}

	public Double getM43() {
		return m43;
	}

	public void setM43(Double m43) {
		this.m43 = m43;
	}

	public Double getM44() {
		return m44;
	}

	public void setM44(Double m44) {
		this.m44 = m44;
	}

	public Double getM45() {
		return m45;
	}

	public void setM45(Double m45) {
		this.m45 = m45;
	}

	public Double getM46() {
		return m46;
	}

	public void setM46(Double m46) {
		this.m46 = m46;
	}

	public Double getM47() {
		return m47;
	}

	public void setM47(Double m47) {
		this.m47 = m47;
	}

	public Double getM48() {
		return m48;
	}

	public void setM48(Double m48) {
		this.m48 = m48;
	}

	public Double getM49() {
		return m49;
	}

	public void setM49(Double m49) {
		this.m49 = m49;
	}

	public Double getM50() {
		return m50;
	}

	public void setM50(Double m50) {
		this.m50 = m50;
	}

	public Double getM51() {
		return m51;
	}

	public void setM51(Double m51) {
		this.m51 = m51;
	}

	public Double getM52() {
		return m52;
	}

	public void setM52(Double m52) {
		this.m52 = m52;
	}

	public Double getM53() {
		return m53;
	}

	public void setM53(Double m53) {
		this.m53 = m53;
	}

	public Double getM54() {
		return m54;
	}

	public void setM54(Double m54) {
		this.m54 = m54;
	}

	public Double getM55() {
		return m55;
	}

	public void setM55(Double m55) {
		this.m55 = m55;
	}

	public Double getM56() {
		return m56;
	}

	public void setM56(Double m56) {
		this.m56 = m56;
	}

	public Double getM57() {
		return m57;
	}

	public void setM57(Double m57) {
		this.m57 = m57;
	}

	public Double getM58() {
		return m58;
	}

	public void setM58(Double m58) {
		this.m58 = m58;
	}

	public Double getM59() {
		return m59;
	}

	public void setM59(Double m59) {
		this.m59 = m59;
	}

	public Double getM60() {
		return m60;
	}

	public void setM60(Double m60) {
		this.m60 = m60;
	}

	public Double getM61() {
		return m61;
	}

	public void setM61(Double m61) {
		this.m61 = m61;
	}

	public Double getM62() {
		return m62;
	}

	public void setM62(Double m62) {
		this.m62 = m62;
	}

	public Double getM63() {
		return m63;
	}

	public void setM63(Double m63) {
		this.m63 = m63;
	}

	public Double getM64() {
		return m64;
	}

	public void setM64(Double m64) {
		this.m64 = m64;
	}

	public Double getM65() {
		return m65;
	}

	public void setM65(Double m65) {
		this.m65 = m65;
	}

	public Double getM66() {
		return m66;
	}

	public void setM66(Double m66) {
		this.m66 = m66;
	}

	public Double getM67() {
		return m67;
	}

	public void setM67(Double m67) {
		this.m67 = m67;
	}

	public Double getM68() {
		return m68;
	}

	public void setM68(Double m68) {
		this.m68 = m68;
	}

	public Double getM69() {
		return m69;
	}

	public void setM69(Double m69) {
		this.m69 = m69;
	}

	public Double getM70() {
		return m70;
	}

	public void setM70(Double m70) {
		this.m70 = m70;
	}

	public Double getM71() {
		return m71;
	}

	public void setM71(Double m71) {
		this.m71 = m71;
	}

	public Double getM72() {
		return m72;
	}

	public void setM72(Double m72) {
		this.m72 = m72;
	}

	public Double getM73() {
		return m73;
	}

	public void setM73(Double m73) {
		this.m73 = m73;
	}

	public Double getM74() {
		return m74;
	}

	public void setM74(Double m74) {
		this.m74 = m74;
	}

	public Double getM75() {
		return m75;
	}

	public void setM75(Double m75) {
		this.m75 = m75;
	}

	public Double getM76() {
		return m76;
	}

	public void setM76(Double m76) {
		this.m76 = m76;
	}

	public Double getM77() {
		return m77;
	}

	public void setM77(Double m77) {
		this.m77 = m77;
	}

	public Double getM78() {
		return m78;
	}

	public void setM78(Double m78) {
		this.m78 = m78;
	}

	public Double getM79() {
		return m79;
	}

	public void setM79(Double m79) {
		this.m79 = m79;
	}

	public Double getM80() {
		return m80;
	}

	public void setM80(Double m80) {
		this.m80 = m80;
	}

	public Double getM81() {
		return m81;
	}

	public void setM81(Double m81) {
		this.m81 = m81;
	}

	public Double getM82() {
		return m82;
	}

	public void setM82(Double m82) {
		this.m82 = m82;
	}

	public Double getM83() {
		return m83;
	}

	public void setM83(Double m83) {
		this.m83 = m83;
	}

	public Double getM84() {
		return m84;
	}

	public void setM84(Double m84) {
		this.m84 = m84;
	}

	public Double getM85() {
		return m85;
	}

	public void setM85(Double m85) {
		this.m85 = m85;
	}

	public Double getM86() {
		return m86;
	}

	public void setM86(Double m86) {
		this.m86 = m86;
	}

	public Double getM87() {
		return m87;
	}

	public void setM87(Double m87) {
		this.m87 = m87;
	}

	public Double getM88() {
		return m88;
	}

	public void setM88(Double m88) {
		this.m88 = m88;
	}

	public Double getM89() {
		return m89;
	}

	public void setM89(Double m89) {
		this.m89 = m89;
	}

	public Double getM90() {
		return m90;
	}

	public void setM90(Double m90) {
		this.m90 = m90;
	}

	public Double getM91() {
		return m91;
	}

	public void setM91(Double m91) {
		this.m91 = m91;
	}

	public Double getM92() {
		return m92;
	}

	public void setM92(Double m92) {
		this.m92 = m92;
	}

	public Double getM93() {
		return m93;
	}

	public void setM93(Double m93) {
		this.m93 = m93;
	}

	public Double getM94() {
		return m94;
	}

	public void setM94(Double m94) {
		this.m94 = m94;
	}

	public Double getM95() {
		return m95;
	}

	public void setM95(Double m95) {
		this.m95 = m95;
	}

	public Double getM96() {
		return m96;
	}

	public void setM96(Double m96) {
		this.m96 = m96;
	}

	public Double getM97() {
		return m97;
	}

	public void setM97(Double m97) {
		this.m97 = m97;
	}

	public Double getM98() {
		return m98;
	}

	public void setM98(Double m98) {
		this.m98 = m98;
	}

	public Double getM99() {
		return m99;
	}

	public void setM99(Double m99) {
		this.m99 = m99;
	}

	public Double getM100() {
		return m100;
	}

	public void setM100(Double m100) {
		this.m100 = m100;
	}

	public Double getM101() {
		return m101;
	}

	public void setM101(Double m101) {
		this.m101 = m101;
	}

	public Double getM102() {
		return m102;
	}

	public void setM102(Double m102) {
		this.m102 = m102;
	}

	public Double getM103() {
		return m103;
	}

	public void setM103(Double m103) {
		this.m103 = m103;
	}

	public Double getM104() {
		return m104;
	}

	public void setM104(Double m104) {
		this.m104 = m104;
	}

	public Double getM105() {
		return m105;
	}

	public void setM105(Double m105) {
		this.m105 = m105;
	}

	public Double getM106() {
		return m106;
	}

	public void setM106(Double m106) {
		this.m106 = m106;
	}

	public Double getM107() {
		return m107;
	}

	public void setM107(Double m107) {
		this.m107 = m107;
	}

	public Double getM108() {
		return m108;
	}

	public void setM108(Double m108) {
		this.m108 = m108;
	}

	public Double getM109() {
		return m109;
	}

	public void setM109(Double m109) {
		this.m109 = m109;
	}

	public Double getM110() {
		return m110;
	}

	public void setM110(Double m110) {
		this.m110 = m110;
	}

	public Double getM111() {
		return m111;
	}

	public void setM111(Double m111) {
		this.m111 = m111;
	}

	public Double getM112() {
		return m112;
	}

	public void setM112(Double m112) {
		this.m112 = m112;
	}

	public Double getM113() {
		return m113;
	}

	public void setM113(Double m113) {
		this.m113 = m113;
	}

	public Double getM114() {
		return m114;
	}

	public void setM114(Double m114) {
		this.m114 = m114;
	}

	public Double getM115() {
		return m115;
	}

	public void setM115(Double m115) {
		this.m115 = m115;
	}

	public Double getM116() {
		return m116;
	}

	public void setM116(Double m116) {
		this.m116 = m116;
	}

	public Double getM117() {
		return m117;
	}

	public void setM117(Double m117) {
		this.m117 = m117;
	}

	public Double getM118() {
		return m118;
	}

	public void setM118(Double m118) {
		this.m118 = m118;
	}

	public Double getM119() {
		return m119;
	}

	public void setM119(Double m119) {
		this.m119 = m119;
	}

	public Double getM120() {
		return m120;
	}

	public void setM120(Double m120) {
		this.m120 = m120;
	}

	public Double getM121() {
		return m121;
	}

	public void setM121(Double m121) {
		this.m121 = m121;
	}

	public Double getM122() {
		return m122;
	}

	public void setM122(Double m122) {
		this.m122 = m122;
	}

	public Double getM123() {
		return m123;
	}

	public void setM123(Double m123) {
		this.m123 = m123;
	}

	public Double getM124() {
		return m124;
	}

	public void setM124(Double m124) {
		this.m124 = m124;
	}

	public Double getM125() {
		return m125;
	}

	public void setM125(Double m125) {
		this.m125 = m125;
	}

	public Double getM126() {
		return m126;
	}

	public void setM126(Double m126) {
		this.m126 = m126;
	}

	public Double getM127() {
		return m127;
	}

	public void setM127(Double m127) {
		this.m127 = m127;
	}

	public Double getM128() {
		return m128;
	}

	public void setM128(Double m128) {
		this.m128 = m128;
	}

	public Double getM129() {
		return m129;
	}

	public void setM129(Double m129) {
		this.m129 = m129;
	}

	public Double getM130() {
		return m130;
	}

	public void setM130(Double m130) {
		this.m130 = m130;
	}

	public Double getM131() {
		return m131;
	}

	public void setM131(Double m131) {
		this.m131 = m131;
	}

	public Double getM132() {
		return m132;
	}

	public void setM132(Double m132) {
		this.m132 = m132;
	}

	public Double getM133() {
		return m133;
	}

	public void setM133(Double m133) {
		this.m133 = m133;
	}

	public Double getM134() {
		return m134;
	}

	public void setM134(Double m134) {
		this.m134 = m134;
	}

	public Double getM135() {
		return m135;
	}

	public void setM135(Double m135) {
		this.m135 = m135;
	}

	public Double getM136() {
		return m136;
	}

	public void setM136(Double m136) {
		this.m136 = m136;
	}

	public Double getM137() {
		return m137;
	}

	public void setM137(Double m137) {
		this.m137 = m137;
	}

	public Double getM138() {
		return m138;
	}

	public void setM138(Double m138) {
		this.m138 = m138;
	}

	public Double getM139() {
		return m139;
	}

	public void setM139(Double m139) {
		this.m139 = m139;
	}

	public Double getM140() {
		return m140;
	}

	public void setM140(Double m140) {
		this.m140 = m140;
	}

	public Double getM141() {
		return m141;
	}

	public void setM141(Double m141) {
		this.m141 = m141;
	}

	public Double getM142() {
		return m142;
	}

	public void setM142(Double m142) {
		this.m142 = m142;
	}

	public Double getM143() {
		return m143;
	}

	public void setM143(Double m143) {
		this.m143 = m143;
	}

	public Double getM144() {
		return m144;
	}

	public void setM144(Double m144) {
		this.m144 = m144;
	}

	public Double getM145() {
		return m145;
	}

	public void setM145(Double m145) {
		this.m145 = m145;
	}

	public Double getM146() {
		return m146;
	}

	public void setM146(Double m146) {
		this.m146 = m146;
	}

	public Double getM147() {
		return m147;
	}

	public void setM147(Double m147) {
		this.m147 = m147;
	}

	public Double getM148() {
		return m148;
	}

	public void setM148(Double m148) {
		this.m148 = m148;
	}

	public Double getM149() {
		return m149;
	}

	public void setM149(Double m149) {
		this.m149 = m149;
	}

	public Double getM150() {
		return m150;
	}

	public void setM150(Double m150) {
		this.m150 = m150;
	}

	public Double getM151() {
		return m151;
	}

	public void setM151(Double m151) {
		this.m151 = m151;
	}

	public Double getM152() {
		return m152;
	}

	public void setM152(Double m152) {
		this.m152 = m152;
	}

	public Double getM153() {
		return m153;
	}

	public void setM153(Double m153) {
		this.m153 = m153;
	}

	public Double getM154() {
		return m154;
	}

	public void setM154(Double m154) {
		this.m154 = m154;
	}

	public Double getM155() {
		return m155;
	}

	public void setM155(Double m155) {
		this.m155 = m155;
	}

	public Double getM156() {
		return m156;
	}

	public void setM156(Double m156) {
		this.m156 = m156;
	}

	public Double getM157() {
		return m157;
	}

	public void setM157(Double m157) {
		this.m157 = m157;
	}

	public Double getM158() {
		return m158;
	}

	public void setM158(Double m158) {
		this.m158 = m158;
	}

	public Double getM159() {
		return m159;
	}

	public void setM159(Double m159) {
		this.m159 = m159;
	}

	public Double getM160() {
		return m160;
	}

	public void setM160(Double m160) {
		this.m160 = m160;
	}

	public Double getM161() {
		return m161;
	}

	public void setM161(Double m161) {
		this.m161 = m161;
	}

	public Double getM162() {
		return m162;
	}

	public void setM162(Double m162) {
		this.m162 = m162;
	}

	public Double getM163() {
		return m163;
	}

	public void setM163(Double m163) {
		this.m163 = m163;
	}

	public Double getM164() {
		return m164;
	}

	public void setM164(Double m164) {
		this.m164 = m164;
	}

	public Double getM165() {
		return m165;
	}

	public void setM165(Double m165) {
		this.m165 = m165;
	}

	public Double getM166() {
		return m166;
	}

	public void setM166(Double m166) {
		this.m166 = m166;
	}

	public Double getM167() {
		return m167;
	}

	public void setM167(Double m167) {
		this.m167 = m167;
	}

	public Double getM168() {
		return m168;
	}

	public void setM168(Double m168) {
		this.m168 = m168;
	}

	public Double getM169() {
		return m169;
	}

	public void setM169(Double m169) {
		this.m169 = m169;
	}

	public Double getM170() {
		return m170;
	}

	public void setM170(Double m170) {
		this.m170 = m170;
	}

	public Double getM171() {
		return m171;
	}

	public void setM171(Double m171) {
		this.m171 = m171;
	}

	public Double getM172() {
		return m172;
	}

	public void setM172(Double m172) {
		this.m172 = m172;
	}

	public Double getM173() {
		return m173;
	}

	public void setM173(Double m173) {
		this.m173 = m173;
	}

	public Double getM174() {
		return m174;
	}

	public void setM174(Double m174) {
		this.m174 = m174;
	}

	public Double getM175() {
		return m175;
	}

	public void setM175(Double m175) {
		this.m175 = m175;
	}

	public Double getM176() {
		return m176;
	}

	public void setM176(Double m176) {
		this.m176 = m176;
	}

	public Double getM177() {
		return m177;
	}

	public void setM177(Double m177) {
		this.m177 = m177;
	}

	public Double getM178() {
		return m178;
	}

	public void setM178(Double m178) {
		this.m178 = m178;
	}

	public Double getM179() {
		return m179;
	}

	public void setM179(Double m179) {
		this.m179 = m179;
	}

	public Double getM180() {
		return m180;
	}

	public void setM180(Double m180) {
		this.m180 = m180;
	}

	public Double getM181() {
		return m181;
	}

	public void setM181(Double m181) {
		this.m181 = m181;
	}

	public Double getM182() {
		return m182;
	}

	public void setM182(Double m182) {
		this.m182 = m182;
	}

	public Double getM183() {
		return m183;
	}

	public void setM183(Double m183) {
		this.m183 = m183;
	}

	public Double getM184() {
		return m184;
	}

	public void setM184(Double m184) {
		this.m184 = m184;
	}

	public Double getM185() {
		return m185;
	}

	public void setM185(Double m185) {
		this.m185 = m185;
	}

	public Double getM186() {
		return m186;
	}

	public void setM186(Double m186) {
		this.m186 = m186;
	}

	public Double getM187() {
		return m187;
	}

	public void setM187(Double m187) {
		this.m187 = m187;
	}

	public Double getM188() {
		return m188;
	}

	public void setM188(Double m188) {
		this.m188 = m188;
	}

	public Double getM189() {
		return m189;
	}

	public void setM189(Double m189) {
		this.m189 = m189;
	}

	public Double getM190() {
		return m190;
	}

	public void setM190(Double m190) {
		this.m190 = m190;
	}

	public Double getM191() {
		return m191;
	}

	public void setM191(Double m191) {
		this.m191 = m191;
	}

	public Double getM192() {
		return m192;
	}

	public void setM192(Double m192) {
		this.m192 = m192;
	}

	public Double getM193() {
		return m193;
	}

	public void setM193(Double m193) {
		this.m193 = m193;
	}

	public Double getM194() {
		return m194;
	}

	public void setM194(Double m194) {
		this.m194 = m194;
	}

	public Double getM195() {
		return m195;
	}

	public void setM195(Double m195) {
		this.m195 = m195;
	}

	public Double getM196() {
		return m196;
	}

	public void setM196(Double m196) {
		this.m196 = m196;
	}

	public Double getM197() {
		return m197;
	}

	public void setM197(Double m197) {
		this.m197 = m197;
	}

	public Double getM198() {
		return m198;
	}

	public void setM198(Double m198) {
		this.m198 = m198;
	}

	public Double getM199() {
		return m199;
	}

	public void setM199(Double m199) {
		this.m199 = m199;
	}

	public Double getM200() {
		return m200;
	}

	public void setM200(Double m200) {
		this.m200 = m200;
	}

	public Double getM201() {
		return m201;
	}

	public void setM201(Double m201) {
		this.m201 = m201;
	}

	public Double getM202() {
		return m202;
	}

	public void setM202(Double m202) {
		this.m202 = m202;
	}

	public Double getM203() {
		return m203;
	}

	public void setM203(Double m203) {
		this.m203 = m203;
	}

	public Double getM204() {
		return m204;
	}

	public void setM204(Double m204) {
		this.m204 = m204;
	}

	public Double getM205() {
		return m205;
	}

	public void setM205(Double m205) {
		this.m205 = m205;
	}

	public Double getM206() {
		return m206;
	}

	public void setM206(Double m206) {
		this.m206 = m206;
	}

	public Double getM207() {
		return m207;
	}

	public void setM207(Double m207) {
		this.m207 = m207;
	}

	public Double getM208() {
		return m208;
	}

	public void setM208(Double m208) {
		this.m208 = m208;
	}

	public Double getM209() {
		return m209;
	}

	public void setM209(Double m209) {
		this.m209 = m209;
	}

	public Double getM210() {
		return m210;
	}

	public void setM210(Double m210) {
		this.m210 = m210;
	}

	public Double getM211() {
		return m211;
	}

	public void setM211(Double m211) {
		this.m211 = m211;
	}

	public Double getM212() {
		return m212;
	}

	public void setM212(Double m212) {
		this.m212 = m212;
	}

	public Double getM213() {
		return m213;
	}

	public void setM213(Double m213) {
		this.m213 = m213;
	}

	public Double getM214() {
		return m214;
	}

	public void setM214(Double m214) {
		this.m214 = m214;
	}

	public Double getM215() {
		return m215;
	}

	public void setM215(Double m215) {
		this.m215 = m215;
	}

	public Double getM216() {
		return m216;
	}

	public void setM216(Double m216) {
		this.m216 = m216;
	}

	public Double getM217() {
		return m217;
	}

	public void setM217(Double m217) {
		this.m217 = m217;
	}

	public Double getM218() {
		return m218;
	}

	public void setM218(Double m218) {
		this.m218 = m218;
	}

	public Double getM219() {
		return m219;
	}

	public void setM219(Double m219) {
		this.m219 = m219;
	}

	public Double getM220() {
		return m220;
	}

	public void setM220(Double m220) {
		this.m220 = m220;
	}

	public Double getM221() {
		return m221;
	}

	public void setM221(Double m221) {
		this.m221 = m221;
	}

	public Double getM222() {
		return m222;
	}

	public void setM222(Double m222) {
		this.m222 = m222;
	}

	public Double getM223() {
		return m223;
	}

	public void setM223(Double m223) {
		this.m223 = m223;
	}

	public Double getM224() {
		return m224;
	}

	public void setM224(Double m224) {
		this.m224 = m224;
	}

	public Double getM225() {
		return m225;
	}

	public void setM225(Double m225) {
		this.m225 = m225;
	}

	public Double getM226() {
		return m226;
	}

	public void setM226(Double m226) {
		this.m226 = m226;
	}

	public Double getM227() {
		return m227;
	}

	public void setM227(Double m227) {
		this.m227 = m227;
	}

	public Double getM228() {
		return m228;
	}

	public void setM228(Double m228) {
		this.m228 = m228;
	}

	public Double getM229() {
		return m229;
	}

	public void setM229(Double m229) {
		this.m229 = m229;
	}

	public Double getM230() {
		return m230;
	}

	public void setM230(Double m230) {
		this.m230 = m230;
	}

	public Double getM231() {
		return m231;
	}

	public void setM231(Double m231) {
		this.m231 = m231;
	}

	public Double getM232() {
		return m232;
	}

	public void setM232(Double m232) {
		this.m232 = m232;
	}

	public Double getM233() {
		return m233;
	}

	public void setM233(Double m233) {
		this.m233 = m233;
	}

	public Double getM234() {
		return m234;
	}

	public void setM234(Double m234) {
		this.m234 = m234;
	}

	public Double getM235() {
		return m235;
	}

	public void setM235(Double m235) {
		this.m235 = m235;
	}

	public Double getM236() {
		return m236;
	}

	public void setM236(Double m236) {
		this.m236 = m236;
	}

	public Double getM237() {
		return m237;
	}

	public void setM237(Double m237) {
		this.m237 = m237;
	}

	public Double getM238() {
		return m238;
	}

	public void setM238(Double m238) {
		this.m238 = m238;
	}

	public Double getM239() {
		return m239;
	}

	public void setM239(Double m239) {
		this.m239 = m239;
	}

	public Double getM240() {
		return m240;
	}

	public void setM240(Double m240) {
		this.m240 = m240;
	}

	public Double getM241() {
		return m241;
	}

	public void setM241(Double m241) {
		this.m241 = m241;
	}

	public Double getM242() {
		return m242;
	}

	public void setM242(Double m242) {
		this.m242 = m242;
	}

	public Double getM243() {
		return m243;
	}

	public void setM243(Double m243) {
		this.m243 = m243;
	}

	public Double getM244() {
		return m244;
	}

	public void setM244(Double m244) {
		this.m244 = m244;
	}

	public Double getM245() {
		return m245;
	}

	public void setM245(Double m245) {
		this.m245 = m245;
	}

	public Double getM246() {
		return m246;
	}

	public void setM246(Double m246) {
		this.m246 = m246;
	}

	public Double getM247() {
		return m247;
	}

	public void setM247(Double m247) {
		this.m247 = m247;
	}

	public Double getM248() {
		return m248;
	}

	public void setM248(Double m248) {
		this.m248 = m248;
	}

	public Double getM249() {
		return m249;
	}

	public void setM249(Double m249) {
		this.m249 = m249;
	}

	public Double getM250() {
		return m250;
	}

	public void setM250(Double m250) {
		this.m250 = m250;
	}

	public Double getM251() {
		return m251;
	}

	public void setM251(Double m251) {
		this.m251 = m251;
	}

	public Double getM252() {
		return m252;
	}

	public void setM252(Double m252) {
		this.m252 = m252;
	}

	public Double getM253() {
		return m253;
	}

	public void setM253(Double m253) {
		this.m253 = m253;
	}

	public Double getM254() {
		return m254;
	}

	public void setM254(Double m254) {
		this.m254 = m254;
	}

	public Double getM255() {
		return m255;
	}

	public void setM255(Double m255) {
		this.m255 = m255;
	}

	public Double getM256() {
		return m256;
	}

	public void setM256(Double m256) {
		this.m256 = m256;
	}

	public Double getM257() {
		return m257;
	}

	public void setM257(Double m257) {
		this.m257 = m257;
	}

	public Double getM258() {
		return m258;
	}

	public void setM258(Double m258) {
		this.m258 = m258;
	}

	public Double getM259() {
		return m259;
	}

	public void setM259(Double m259) {
		this.m259 = m259;
	}

	public Double getM260() {
		return m260;
	}

	public void setM260(Double m260) {
		this.m260 = m260;
	}

	public Double getM261() {
		return m261;
	}

	public void setM261(Double m261) {
		this.m261 = m261;
	}

	public Double getM262() {
		return m262;
	}

	public void setM262(Double m262) {
		this.m262 = m262;
	}

	public Double getM263() {
		return m263;
	}

	public void setM263(Double m263) {
		this.m263 = m263;
	}

	public Double getM264() {
		return m264;
	}

	public void setM264(Double m264) {
		this.m264 = m264;
	}

	public Double getM265() {
		return m265;
	}

	public void setM265(Double m265) {
		this.m265 = m265;
	}

	public Double getM266() {
		return m266;
	}

	public void setM266(Double m266) {
		this.m266 = m266;
	}

	public Double getM267() {
		return m267;
	}

	public void setM267(Double m267) {
		this.m267 = m267;
	}

	public Double getM268() {
		return m268;
	}

	public void setM268(Double m268) {
		this.m268 = m268;
	}

	public Double getM269() {
		return m269;
	}

	public void setM269(Double m269) {
		this.m269 = m269;
	}

	public Double getM270() {
		return m270;
	}

	public void setM270(Double m270) {
		this.m270 = m270;
	}

	public Double getM271() {
		return m271;
	}

	public void setM271(Double m271) {
		this.m271 = m271;
	}

	public Double getM272() {
		return m272;
	}

	public void setM272(Double m272) {
		this.m272 = m272;
	}

	public Double getM273() {
		return m273;
	}

	public void setM273(Double m273) {
		this.m273 = m273;
	}

	public Double getM274() {
		return m274;
	}

	public void setM274(Double m274) {
		this.m274 = m274;
	}

	public Double getM275() {
		return m275;
	}

	public void setM275(Double m275) {
		this.m275 = m275;
	}

	public Double getM276() {
		return m276;
	}

	public void setM276(Double m276) {
		this.m276 = m276;
	}

	public Double getM277() {
		return m277;
	}

	public void setM277(Double m277) {
		this.m277 = m277;
	}

	public Double getM278() {
		return m278;
	}

	public void setM278(Double m278) {
		this.m278 = m278;
	}

	public Double getM279() {
		return m279;
	}

	public void setM279(Double m279) {
		this.m279 = m279;
	}

	public Double getM280() {
		return m280;
	}

	public void setM280(Double m280) {
		this.m280 = m280;
	}

	public Double getM281() {
		return m281;
	}

	public void setM281(Double m281) {
		this.m281 = m281;
	}

	public Double getM282() {
		return m282;
	}

	public void setM282(Double m282) {
		this.m282 = m282;
	}

	public Double getM283() {
		return m283;
	}

	public void setM283(Double m283) {
		this.m283 = m283;
	}

	public Double getM284() {
		return m284;
	}

	public void setM284(Double m284) {
		this.m284 = m284;
	}

	public Double getM285() {
		return m285;
	}

	public void setM285(Double m285) {
		this.m285 = m285;
	}

	public Double getM286() {
		return m286;
	}

	public void setM286(Double m286) {
		this.m286 = m286;
	}

	public Double getM287() {
		return m287;
	}

	public void setM287(Double m287) {
		this.m287 = m287;
	}

	public Double getM288() {
		return m288;
	}

	public void setM288(Double m288) {
		this.m288 = m288;
	}

	public Double getM289() {
		return m289;
	}

	public void setM289(Double m289) {
		this.m289 = m289;
	}

	public Double getM290() {
		return m290;
	}

	public void setM290(Double m290) {
		this.m290 = m290;
	}

	public Double getM291() {
		return m291;
	}

	public void setM291(Double m291) {
		this.m291 = m291;
	}

	public Double getM292() {
		return m292;
	}

	public void setM292(Double m292) {
		this.m292 = m292;
	}

	public Double getM293() {
		return m293;
	}

	public void setM293(Double m293) {
		this.m293 = m293;
	}

	public Double getM294() {
		return m294;
	}

	public void setM294(Double m294) {
		this.m294 = m294;
	}

	public Double getM295() {
		return m295;
	}

	public void setM295(Double m295) {
		this.m295 = m295;
	}

	public Double getM296() {
		return m296;
	}

	public void setM296(Double m296) {
		this.m296 = m296;
	}

	public Double getM297() {
		return m297;
	}

	public void setM297(Double m297) {
		this.m297 = m297;
	}

	public Double getM298() {
		return m298;
	}

	public void setM298(Double m298) {
		this.m298 = m298;
	}

	public Double getM299() {
		return m299;
	}

	public void setM299(Double m299) {
		this.m299 = m299;
	}

	public Double getM300() {
		return m300;
	}

	public void setM300(Double m300) {
		this.m300 = m300;
	}

	public Double getM301() {
		return m301;
	}

	public void setM301(Double m301) {
		this.m301 = m301;
	}

	public Double getM302() {
		return m302;
	}

	public void setM302(Double m302) {
		this.m302 = m302;
	}

	public Double getM303() {
		return m303;
	}

	public void setM303(Double m303) {
		this.m303 = m303;
	}

	public Double getM304() {
		return m304;
	}

	public void setM304(Double m304) {
		this.m304 = m304;
	}

	public Double getM305() {
		return m305;
	}

	public void setM305(Double m305) {
		this.m305 = m305;
	}

	public Double getM306() {
		return m306;
	}

	public void setM306(Double m306) {
		this.m306 = m306;
	}

	public Double getM307() {
		return m307;
	}

	public void setM307(Double m307) {
		this.m307 = m307;
	}

	public Double getM308() {
		return m308;
	}

	public void setM308(Double m308) {
		this.m308 = m308;
	}

	public Double getM309() {
		return m309;
	}

	public void setM309(Double m309) {
		this.m309 = m309;
	}

	public Double getM310() {
		return m310;
	}

	public void setM310(Double m310) {
		this.m310 = m310;
	}

	public Double getM311() {
		return m311;
	}

	public void setM311(Double m311) {
		this.m311 = m311;
	}

	public Double getM312() {
		return m312;
	}

	public void setM312(Double m312) {
		this.m312 = m312;
	}

	public Double getM313() {
		return m313;
	}

	public void setM313(Double m313) {
		this.m313 = m313;
	}

	public Double getM314() {
		return m314;
	}

	public void setM314(Double m314) {
		this.m314 = m314;
	}

	public Double getM315() {
		return m315;
	}

	public void setM315(Double m315) {
		this.m315 = m315;
	}

	public Double getM316() {
		return m316;
	}

	public void setM316(Double m316) {
		this.m316 = m316;
	}

	public Double getM317() {
		return m317;
	}

	public void setM317(Double m317) {
		this.m317 = m317;
	}

	public Double getM318() {
		return m318;
	}

	public void setM318(Double m318) {
		this.m318 = m318;
	}

	public Double getM319() {
		return m319;
	}

	public void setM319(Double m319) {
		this.m319 = m319;
	}

	public Double getM320() {
		return m320;
	}

	public void setM320(Double m320) {
		this.m320 = m320;
	}

	public Double getM321() {
		return m321;
	}

	public void setM321(Double m321) {
		this.m321 = m321;
	}

	public Double getM322() {
		return m322;
	}

	public void setM322(Double m322) {
		this.m322 = m322;
	}

	public Double getM323() {
		return m323;
	}

	public void setM323(Double m323) {
		this.m323 = m323;
	}

	public Double getM324() {
		return m324;
	}

	public void setM324(Double m324) {
		this.m324 = m324;
	}

	public Double getM325() {
		return m325;
	}

	public void setM325(Double m325) {
		this.m325 = m325;
	}

	public Double getM326() {
		return m326;
	}

	public void setM326(Double m326) {
		this.m326 = m326;
	}

	public Double getM327() {
		return m327;
	}

	public void setM327(Double m327) {
		this.m327 = m327;
	}

	public Double getM328() {
		return m328;
	}

	public void setM328(Double m328) {
		this.m328 = m328;
	}

	public Double getM329() {
		return m329;
	}

	public void setM329(Double m329) {
		this.m329 = m329;
	}

	public Double getM330() {
		return m330;
	}

	public void setM330(Double m330) {
		this.m330 = m330;
	}

	public Double getM331() {
		return m331;
	}

	public void setM331(Double m331) {
		this.m331 = m331;
	}

	public Double getM332() {
		return m332;
	}

	public void setM332(Double m332) {
		this.m332 = m332;
	}

	public Double getM333() {
		return m333;
	}

	public void setM333(Double m333) {
		this.m333 = m333;
	}

	public Double getM334() {
		return m334;
	}

	public void setM334(Double m334) {
		this.m334 = m334;
	}

	public Double getM335() {
		return m335;
	}

	public void setM335(Double m335) {
		this.m335 = m335;
	}

	public Double getM336() {
		return m336;
	}

	public void setM336(Double m336) {
		this.m336 = m336;
	}

	public Double getM337() {
		return m337;
	}

	public void setM337(Double m337) {
		this.m337 = m337;
	}

	public Double getM338() {
		return m338;
	}

	public void setM338(Double m338) {
		this.m338 = m338;
	}

	public Double getM339() {
		return m339;
	}

	public void setM339(Double m339) {
		this.m339 = m339;
	}

	public Double getM340() {
		return m340;
	}

	public void setM340(Double m340) {
		this.m340 = m340;
	}

	public Double getM341() {
		return m341;
	}

	public void setM341(Double m341) {
		this.m341 = m341;
	}

	public Double getM342() {
		return m342;
	}

	public void setM342(Double m342) {
		this.m342 = m342;
	}

	public Double getM343() {
		return m343;
	}

	public void setM343(Double m343) {
		this.m343 = m343;
	}

	public Double getM344() {
		return m344;
	}

	public void setM344(Double m344) {
		this.m344 = m344;
	}

	public Double getM345() {
		return m345;
	}

	public void setM345(Double m345) {
		this.m345 = m345;
	}

	public Double getM346() {
		return m346;
	}

	public void setM346(Double m346) {
		this.m346 = m346;
	}

	public Double getM347() {
		return m347;
	}

	public void setM347(Double m347) {
		this.m347 = m347;
	}

	public Double getM348() {
		return m348;
	}

	public void setM348(Double m348) {
		this.m348 = m348;
	}

	public Double getM349() {
		return m349;
	}

	public void setM349(Double m349) {
		this.m349 = m349;
	}

	public Double getM350() {
		return m350;
	}

	public void setM350(Double m350) {
		this.m350 = m350;
	}

	public Double getM351() {
		return m351;
	}

	public void setM351(Double m351) {
		this.m351 = m351;
	}

	public Double getM352() {
		return m352;
	}

	public void setM352(Double m352) {
		this.m352 = m352;
	}

	public Double getM353() {
		return m353;
	}

	public void setM353(Double m353) {
		this.m353 = m353;
	}

	public Double getM354() {
		return m354;
	}

	public void setM354(Double m354) {
		this.m354 = m354;
	}

	public Double getM355() {
		return m355;
	}

	public void setM355(Double m355) {
		this.m355 = m355;
	}

	public Double getM356() {
		return m356;
	}

	public void setM356(Double m356) {
		this.m356 = m356;
	}

	public Double getM357() {
		return m357;
	}

	public void setM357(Double m357) {
		this.m357 = m357;
	}

	public Double getM358() {
		return m358;
	}

	public void setM358(Double m358) {
		this.m358 = m358;
	}

	public Double getM359() {
		return m359;
	}

	public void setM359(Double m359) {
		this.m359 = m359;
	}

	public Double getM360() {
		return m360;
	}

	public void setM360(Double m360) {
		this.m360 = m360;
	}

	public Double getM361() {
		return m361;
	}

	public void setM361(Double m361) {
		this.m361 = m361;
	}

	public Double getM362() {
		return m362;
	}

	public void setM362(Double m362) {
		this.m362 = m362;
	}

	public Double getM363() {
		return m363;
	}

	public void setM363(Double m363) {
		this.m363 = m363;
	}

	public Double getM364() {
		return m364;
	}

	public void setM364(Double m364) {
		this.m364 = m364;
	}

	public Double getM365() {
		return m365;
	}

	public void setM365(Double m365) {
		this.m365 = m365;
	}

	public Double getM366() {
		return m366;
	}

	public void setM366(Double m366) {
		this.m366 = m366;
	}

	public Double getM367() {
		return m367;
	}

	public void setM367(Double m367) {
		this.m367 = m367;
	}

	public Double getM368() {
		return m368;
	}

	public void setM368(Double m368) {
		this.m368 = m368;
	}

	public Double getM369() {
		return m369;
	}

	public void setM369(Double m369) {
		this.m369 = m369;
	}

	public Double getM370() {
		return m370;
	}

	public void setM370(Double m370) {
		this.m370 = m370;
	}

	public Double getM371() {
		return m371;
	}

	public void setM371(Double m371) {
		this.m371 = m371;
	}

	public Double getM372() {
		return m372;
	}

	public void setM372(Double m372) {
		this.m372 = m372;
	}

	public Double getM373() {
		return m373;
	}

	public void setM373(Double m373) {
		this.m373 = m373;
	}

	public Double getM374() {
		return m374;
	}

	public void setM374(Double m374) {
		this.m374 = m374;
	}

	public Double getM375() {
		return m375;
	}

	public void setM375(Double m375) {
		this.m375 = m375;
	}

	public Double getM376() {
		return m376;
	}

	public void setM376(Double m376) {
		this.m376 = m376;
	}

	public Double getM377() {
		return m377;
	}

	public void setM377(Double m377) {
		this.m377 = m377;
	}

	public Double getM378() {
		return m378;
	}

	public void setM378(Double m378) {
		this.m378 = m378;
	}

	public Double getM379() {
		return m379;
	}

	public void setM379(Double m379) {
		this.m379 = m379;
	}

	public Double getM380() {
		return m380;
	}

	public void setM380(Double m380) {
		this.m380 = m380;
	}

	public Double getM381() {
		return m381;
	}

	public void setM381(Double m381) {
		this.m381 = m381;
	}

	public Double getM382() {
		return m382;
	}

	public void setM382(Double m382) {
		this.m382 = m382;
	}

	public Double getM383() {
		return m383;
	}

	public void setM383(Double m383) {
		this.m383 = m383;
	}

	public Double getM384() {
		return m384;
	}

	public void setM384(Double m384) {
		this.m384 = m384;
	}

	public Double getM385() {
		return m385;
	}

	public void setM385(Double m385) {
		this.m385 = m385;
	}

	public Double getM386() {
		return m386;
	}

	public void setM386(Double m386) {
		this.m386 = m386;
	}

	public Double getM387() {
		return m387;
	}

	public void setM387(Double m387) {
		this.m387 = m387;
	}

	public Double getM388() {
		return m388;
	}

	public void setM388(Double m388) {
		this.m388 = m388;
	}

	public Double getM389() {
		return m389;
	}

	public void setM389(Double m389) {
		this.m389 = m389;
	}

	public Double getM390() {
		return m390;
	}

	public void setM390(Double m390) {
		this.m390 = m390;
	}

	public Double getM391() {
		return m391;
	}

	public void setM391(Double m391) {
		this.m391 = m391;
	}

	public Double getM392() {
		return m392;
	}

	public void setM392(Double m392) {
		this.m392 = m392;
	}

	public Double getM393() {
		return m393;
	}

	public void setM393(Double m393) {
		this.m393 = m393;
	}

	public Double getM394() {
		return m394;
	}

	public void setM394(Double m394) {
		this.m394 = m394;
	}

	public Double getM395() {
		return m395;
	}

	public void setM395(Double m395) {
		this.m395 = m395;
	}

	public Double getM396() {
		return m396;
	}

	public void setM396(Double m396) {
		this.m396 = m396;
	}

	public Double getM397() {
		return m397;
	}

	public void setM397(Double m397) {
		this.m397 = m397;
	}

	public Double getM398() {
		return m398;
	}

	public void setM398(Double m398) {
		this.m398 = m398;
	}

	public Double getM399() {
		return m399;
	}

	public void setM399(Double m399) {
		this.m399 = m399;
	}

	public Double getM400() {
		return m400;
	}

	public void setM400(Double m400) {
		this.m400 = m400;
	}

	public Double getM401() {
		return m401;
	}

	public void setM401(Double m401) {
		this.m401 = m401;
	}

	public Double getM402() {
		return m402;
	}

	public void setM402(Double m402) {
		this.m402 = m402;
	}

	public Double getM403() {
		return m403;
	}

	public void setM403(Double m403) {
		this.m403 = m403;
	}

	public Double getM404() {
		return m404;
	}

	public void setM404(Double m404) {
		this.m404 = m404;
	}

	public Double getM405() {
		return m405;
	}

	public void setM405(Double m405) {
		this.m405 = m405;
	}

	public Double getM406() {
		return m406;
	}

	public void setM406(Double m406) {
		this.m406 = m406;
	}

	public Double getM407() {
		return m407;
	}

	public void setM407(Double m407) {
		this.m407 = m407;
	}

	public Double getM408() {
		return m408;
	}

	public void setM408(Double m408) {
		this.m408 = m408;
	}

	public Double getM409() {
		return m409;
	}

	public void setM409(Double m409) {
		this.m409 = m409;
	}

	public Double getM410() {
		return m410;
	}

	public void setM410(Double m410) {
		this.m410 = m410;
	}

	public Double getM411() {
		return m411;
	}

	public void setM411(Double m411) {
		this.m411 = m411;
	}

	public Double getM412() {
		return m412;
	}

	public void setM412(Double m412) {
		this.m412 = m412;
	}

	public Double getM413() {
		return m413;
	}

	public void setM413(Double m413) {
		this.m413 = m413;
	}

	public Double getM414() {
		return m414;
	}

	public void setM414(Double m414) {
		this.m414 = m414;
	}

	public Double getM415() {
		return m415;
	}

	public void setM415(Double m415) {
		this.m415 = m415;
	}

	public Double getM416() {
		return m416;
	}

	public void setM416(Double m416) {
		this.m416 = m416;
	}

	public Double getM417() {
		return m417;
	}

	public void setM417(Double m417) {
		this.m417 = m417;
	}

	public Double getM418() {
		return m418;
	}

	public void setM418(Double m418) {
		this.m418 = m418;
	}

	public Double getM419() {
		return m419;
	}

	public void setM419(Double m419) {
		this.m419 = m419;
	}

	public Double getM420() {
		return m420;
	}

	public void setM420(Double m420) {
		this.m420 = m420;
	}

	public Double getM421() {
		return m421;
	}

	public void setM421(Double m421) {
		this.m421 = m421;
	}

	public Double getM422() {
		return m422;
	}

	public void setM422(Double m422) {
		this.m422 = m422;
	}

	public Double getM423() {
		return m423;
	}

	public void setM423(Double m423) {
		this.m423 = m423;
	}

	public Double getM424() {
		return m424;
	}

	public void setM424(Double m424) {
		this.m424 = m424;
	}

	public Double getM425() {
		return m425;
	}

	public void setM425(Double m425) {
		this.m425 = m425;
	}

	public Double getM426() {
		return m426;
	}

	public void setM426(Double m426) {
		this.m426 = m426;
	}

	public Double getM427() {
		return m427;
	}

	public void setM427(Double m427) {
		this.m427 = m427;
	}

	public Double getM428() {
		return m428;
	}

	public void setM428(Double m428) {
		this.m428 = m428;
	}

	public Double getM429() {
		return m429;
	}

	public void setM429(Double m429) {
		this.m429 = m429;
	}

	public Double getM430() {
		return m430;
	}

	public void setM430(Double m430) {
		this.m430 = m430;
	}

	public Double getM431() {
		return m431;
	}

	public void setM431(Double m431) {
		this.m431 = m431;
	}

	public Double getM432() {
		return m432;
	}

	public void setM432(Double m432) {
		this.m432 = m432;
	}

	public Double getM433() {
		return m433;
	}

	public void setM433(Double m433) {
		this.m433 = m433;
	}

	public Double getM434() {
		return m434;
	}

	public void setM434(Double m434) {
		this.m434 = m434;
	}

	public Double getM435() {
		return m435;
	}

	public void setM435(Double m435) {
		this.m435 = m435;
	}

	public Double getM436() {
		return m436;
	}

	public void setM436(Double m436) {
		this.m436 = m436;
	}

	public Double getM437() {
		return m437;
	}

	public void setM437(Double m437) {
		this.m437 = m437;
	}

	public Double getM438() {
		return m438;
	}

	public void setM438(Double m438) {
		this.m438 = m438;
	}

	public Double getM439() {
		return m439;
	}

	public void setM439(Double m439) {
		this.m439 = m439;
	}

	public Double getM440() {
		return m440;
	}

	public void setM440(Double m440) {
		this.m440 = m440;
	}

	public Double getM441() {
		return m441;
	}

	public void setM441(Double m441) {
		this.m441 = m441;
	}

	public Double getM442() {
		return m442;
	}

	public void setM442(Double m442) {
		this.m442 = m442;
	}

	public Double getM443() {
		return m443;
	}

	public void setM443(Double m443) {
		this.m443 = m443;
	}

	public Double getM444() {
		return m444;
	}

	public void setM444(Double m444) {
		this.m444 = m444;
	}

	public Double getM445() {
		return m445;
	}

	public void setM445(Double m445) {
		this.m445 = m445;
	}

	public Double getM446() {
		return m446;
	}

	public void setM446(Double m446) {
		this.m446 = m446;
	}

	public Double getM447() {
		return m447;
	}

	public void setM447(Double m447) {
		this.m447 = m447;
	}

	public Double getM448() {
		return m448;
	}

	public void setM448(Double m448) {
		this.m448 = m448;
	}

	public Double getM449() {
		return m449;
	}

	public void setM449(Double m449) {
		this.m449 = m449;
	}

	public Double getM450() {
		return m450;
	}

	public void setM450(Double m450) {
		this.m450 = m450;
	}

	public Double getM451() {
		return m451;
	}

	public void setM451(Double m451) {
		this.m451 = m451;
	}

	public Double getM452() {
		return m452;
	}

	public void setM452(Double m452) {
		this.m452 = m452;
	}

	public Double getM453() {
		return m453;
	}

	public void setM453(Double m453) {
		this.m453 = m453;
	}

	public Double getM454() {
		return m454;
	}

	public void setM454(Double m454) {
		this.m454 = m454;
	}

	public Double getM455() {
		return m455;
	}

	public void setM455(Double m455) {
		this.m455 = m455;
	}

	public Double getM456() {
		return m456;
	}

	public void setM456(Double m456) {
		this.m456 = m456;
	}

	public Double getM457() {
		return m457;
	}

	public void setM457(Double m457) {
		this.m457 = m457;
	}

	public Double getM458() {
		return m458;
	}

	public void setM458(Double m458) {
		this.m458 = m458;
	}

	public Double getM459() {
		return m459;
	}

	public void setM459(Double m459) {
		this.m459 = m459;
	}

	public Double getM460() {
		return m460;
	}

	public void setM460(Double m460) {
		this.m460 = m460;
	}

	public Double getM461() {
		return m461;
	}

	public void setM461(Double m461) {
		this.m461 = m461;
	}

	public Double getM462() {
		return m462;
	}

	public void setM462(Double m462) {
		this.m462 = m462;
	}

	public Double getM463() {
		return m463;
	}

	public void setM463(Double m463) {
		this.m463 = m463;
	}

	public Double getM464() {
		return m464;
	}

	public void setM464(Double m464) {
		this.m464 = m464;
	}

	public Double getM465() {
		return m465;
	}

	public void setM465(Double m465) {
		this.m465 = m465;
	}

	public Double getM466() {
		return m466;
	}

	public void setM466(Double m466) {
		this.m466 = m466;
	}

	public Double getM467() {
		return m467;
	}

	public void setM467(Double m467) {
		this.m467 = m467;
	}

	public Double getM468() {
		return m468;
	}

	public void setM468(Double m468) {
		this.m468 = m468;
	}

	public Double getM469() {
		return m469;
	}

	public void setM469(Double m469) {
		this.m469 = m469;
	}

	public Double getM470() {
		return m470;
	}

	public void setM470(Double m470) {
		this.m470 = m470;
	}

	public Double getM471() {
		return m471;
	}

	public void setM471(Double m471) {
		this.m471 = m471;
	}

	public Double getM472() {
		return m472;
	}

	public void setM472(Double m472) {
		this.m472 = m472;
	}

	public Double getM473() {
		return m473;
	}

	public void setM473(Double m473) {
		this.m473 = m473;
	}

	public Double getM474() {
		return m474;
	}

	public void setM474(Double m474) {
		this.m474 = m474;
	}

	public Double getM475() {
		return m475;
	}

	public void setM475(Double m475) {
		this.m475 = m475;
	}

	public Double getM476() {
		return m476;
	}

	public void setM476(Double m476) {
		this.m476 = m476;
	}

	public Double getM477() {
		return m477;
	}

	public void setM477(Double m477) {
		this.m477 = m477;
	}

	public Double getM478() {
		return m478;
	}

	public void setM478(Double m478) {
		this.m478 = m478;
	}

	public Double getM479() {
		return m479;
	}

	public void setM479(Double m479) {
		this.m479 = m479;
	}

	public Double getM480() {
		return m480;
	}

	public void setM480(Double m480) {
		this.m480 = m480;
	}

	public Double getM481() {
		return m481;
	}

	public void setM481(Double m481) {
		this.m481 = m481;
	}

	public Double getM482() {
		return m482;
	}

	public void setM482(Double m482) {
		this.m482 = m482;
	}

	public Double getM483() {
		return m483;
	}

	public void setM483(Double m483) {
		this.m483 = m483;
	}

	public Double getM484() {
		return m484;
	}

	public void setM484(Double m484) {
		this.m484 = m484;
	}

	public Double getM485() {
		return m485;
	}

	public void setM485(Double m485) {
		this.m485 = m485;
	}

	public Double getM486() {
		return m486;
	}

	public void setM486(Double m486) {
		this.m486 = m486;
	}

	public Double getM487() {
		return m487;
	}

	public void setM487(Double m487) {
		this.m487 = m487;
	}

	public Double getM488() {
		return m488;
	}

	public void setM488(Double m488) {
		this.m488 = m488;
	}

	public Double getM489() {
		return m489;
	}

	public void setM489(Double m489) {
		this.m489 = m489;
	}

	public Double getM490() {
		return m490;
	}

	public void setM490(Double m490) {
		this.m490 = m490;
	}

	public Double getM491() {
		return m491;
	}

	public void setM491(Double m491) {
		this.m491 = m491;
	}

	public Double getM492() {
		return m492;
	}

	public void setM492(Double m492) {
		this.m492 = m492;
	}

	public Double getM493() {
		return m493;
	}

	public void setM493(Double m493) {
		this.m493 = m493;
	}

	public Double getM494() {
		return m494;
	}

	public void setM494(Double m494) {
		this.m494 = m494;
	}

	public Double getM495() {
		return m495;
	}

	public void setM495(Double m495) {
		this.m495 = m495;
	}

	public Double getM496() {
		return m496;
	}

	public void setM496(Double m496) {
		this.m496 = m496;
	}

	public Double getM497() {
		return m497;
	}

	public void setM497(Double m497) {
		this.m497 = m497;
	}

	public Double getM498() {
		return m498;
	}

	public void setM498(Double m498) {
		this.m498 = m498;
	}

	public Double getM499() {
		return m499;
	}

	public void setM499(Double m499) {
		this.m499 = m499;
	}

	public Double getM500() {
		return m500;
	}

	public void setM500(Double m500) {
		this.m500 = m500;
	}

	public Double getM501() {
		return m501;
	}

	public void setM501(Double m501) {
		this.m501 = m501;
	}

	public Double getM502() {
		return m502;
	}

	public void setM502(Double m502) {
		this.m502 = m502;
	}

	public Double getM503() {
		return m503;
	}

	public void setM503(Double m503) {
		this.m503 = m503;
	}

	public Double getM504() {
		return m504;
	}

	public void setM504(Double m504) {
		this.m504 = m504;
	}

	public Double getM505() {
		return m505;
	}

	public void setM505(Double m505) {
		this.m505 = m505;
	}

	public Double getM506() {
		return m506;
	}

	public void setM506(Double m506) {
		this.m506 = m506;
	}

	public Double getM507() {
		return m507;
	}

	public void setM507(Double m507) {
		this.m507 = m507;
	}

	public Double getM508() {
		return m508;
	}

	public void setM508(Double m508) {
		this.m508 = m508;
	}

	public Double getM509() {
		return m509;
	}

	public void setM509(Double m509) {
		this.m509 = m509;
	}

	public Double getM510() {
		return m510;
	}

	public void setM510(Double m510) {
		this.m510 = m510;
	}

	public Double getM511() {
		return m511;
	}

	public void setM511(Double m511) {
		this.m511 = m511;
	}

	public Double getM512() {
		return m512;
	}

	public void setM512(Double m512) {
		this.m512 = m512;
	}

	public Double getM513() {
		return m513;
	}

	public void setM513(Double m513) {
		this.m513 = m513;
	}

	public Double getM514() {
		return m514;
	}

	public void setM514(Double m514) {
		this.m514 = m514;
	}

	public Double getM515() {
		return m515;
	}

	public void setM515(Double m515) {
		this.m515 = m515;
	}

	public Double getM516() {
		return m516;
	}

	public void setM516(Double m516) {
		this.m516 = m516;
	}

	public Double getM517() {
		return m517;
	}

	public void setM517(Double m517) {
		this.m517 = m517;
	}

	public Double getM518() {
		return m518;
	}

	public void setM518(Double m518) {
		this.m518 = m518;
	}

	public Double getM519() {
		return m519;
	}

	public void setM519(Double m519) {
		this.m519 = m519;
	}

	public Double getM520() {
		return m520;
	}

	public void setM520(Double m520) {
		this.m520 = m520;
	}

	public Double getM521() {
		return m521;
	}

	public void setM521(Double m521) {
		this.m521 = m521;
	}

	public Double getM522() {
		return m522;
	}

	public void setM522(Double m522) {
		this.m522 = m522;
	}

	public Double getM523() {
		return m523;
	}

	public void setM523(Double m523) {
		this.m523 = m523;
	}

	public Double getM524() {
		return m524;
	}

	public void setM524(Double m524) {
		this.m524 = m524;
	}

	public Double getM525() {
		return m525;
	}

	public void setM525(Double m525) {
		this.m525 = m525;
	}

	public Double getM526() {
		return m526;
	}

	public void setM526(Double m526) {
		this.m526 = m526;
	}

	public Double getM527() {
		return m527;
	}

	public void setM527(Double m527) {
		this.m527 = m527;
	}

	public Double getM528() {
		return m528;
	}

	public void setM528(Double m528) {
		this.m528 = m528;
	}

	public Double getM529() {
		return m529;
	}

	public void setM529(Double m529) {
		this.m529 = m529;
	}

	public Double getM530() {
		return m530;
	}

	public void setM530(Double m530) {
		this.m530 = m530;
	}

	public Double getM531() {
		return m531;
	}

	public void setM531(Double m531) {
		this.m531 = m531;
	}

	public Double getM532() {
		return m532;
	}

	public void setM532(Double m532) {
		this.m532 = m532;
	}

	public Double getM533() {
		return m533;
	}

	public void setM533(Double m533) {
		this.m533 = m533;
	}

	public Double getM534() {
		return m534;
	}

	public void setM534(Double m534) {
		this.m534 = m534;
	}

	public Double getM535() {
		return m535;
	}

	public void setM535(Double m535) {
		this.m535 = m535;
	}

	public Double getM536() {
		return m536;
	}

	public void setM536(Double m536) {
		this.m536 = m536;
	}

	public Double getM537() {
		return m537;
	}

	public void setM537(Double m537) {
		this.m537 = m537;
	}

	public Double getM538() {
		return m538;
	}

	public void setM538(Double m538) {
		this.m538 = m538;
	}

	public Double getM539() {
		return m539;
	}

	public void setM539(Double m539) {
		this.m539 = m539;
	}

	public Double getM540() {
		return m540;
	}

	public void setM540(Double m540) {
		this.m540 = m540;
	}

	public Double getM541() {
		return m541;
	}

	public void setM541(Double m541) {
		this.m541 = m541;
	}

	public Double getM542() {
		return m542;
	}

	public void setM542(Double m542) {
		this.m542 = m542;
	}

	public Double getM543() {
		return m543;
	}

	public void setM543(Double m543) {
		this.m543 = m543;
	}

	public Double getM544() {
		return m544;
	}

	public void setM544(Double m544) {
		this.m544 = m544;
	}

	public Double getM545() {
		return m545;
	}

	public void setM545(Double m545) {
		this.m545 = m545;
	}

	public Double getM546() {
		return m546;
	}

	public void setM546(Double m546) {
		this.m546 = m546;
	}

	public Double getM547() {
		return m547;
	}

	public void setM547(Double m547) {
		this.m547 = m547;
	}

	public Double getM548() {
		return m548;
	}

	public void setM548(Double m548) {
		this.m548 = m548;
	}

	public Double getM549() {
		return m549;
	}

	public void setM549(Double m549) {
		this.m549 = m549;
	}

	public Double getM550() {
		return m550;
	}

	public void setM550(Double m550) {
		this.m550 = m550;
	}

	public Double getM551() {
		return m551;
	}

	public void setM551(Double m551) {
		this.m551 = m551;
	}

	public Double getM552() {
		return m552;
	}

	public void setM552(Double m552) {
		this.m552 = m552;
	}

	public Double getM553() {
		return m553;
	}

	public void setM553(Double m553) {
		this.m553 = m553;
	}

	public Double getM554() {
		return m554;
	}

	public void setM554(Double m554) {
		this.m554 = m554;
	}

	public Double getM555() {
		return m555;
	}

	public void setM555(Double m555) {
		this.m555 = m555;
	}

	public Double getM556() {
		return m556;
	}

	public void setM556(Double m556) {
		this.m556 = m556;
	}

	public Double getM557() {
		return m557;
	}

	public void setM557(Double m557) {
		this.m557 = m557;
	}

	public Double getM558() {
		return m558;
	}

	public void setM558(Double m558) {
		this.m558 = m558;
	}

	public Double getM559() {
		return m559;
	}

	public void setM559(Double m559) {
		this.m559 = m559;
	}

	public Double getM560() {
		return m560;
	}

	public void setM560(Double m560) {
		this.m560 = m560;
	}

	public Double getM561() {
		return m561;
	}

	public void setM561(Double m561) {
		this.m561 = m561;
	}

	public Double getM562() {
		return m562;
	}

	public void setM562(Double m562) {
		this.m562 = m562;
	}

	public Double getM563() {
		return m563;
	}

	public void setM563(Double m563) {
		this.m563 = m563;
	}

	public Double getM564() {
		return m564;
	}

	public void setM564(Double m564) {
		this.m564 = m564;
	}

	public Double getM565() {
		return m565;
	}

	public void setM565(Double m565) {
		this.m565 = m565;
	}

	public Double getM566() {
		return m566;
	}

	public void setM566(Double m566) {
		this.m566 = m566;
	}

	public Double getM567() {
		return m567;
	}

	public void setM567(Double m567) {
		this.m567 = m567;
	}

	public Double getM568() {
		return m568;
	}

	public void setM568(Double m568) {
		this.m568 = m568;
	}

	public Double getM569() {
		return m569;
	}

	public void setM569(Double m569) {
		this.m569 = m569;
	}

	public Double getM570() {
		return m570;
	}

	public void setM570(Double m570) {
		this.m570 = m570;
	}

	public Double getM571() {
		return m571;
	}

	public void setM571(Double m571) {
		this.m571 = m571;
	}

	public Double getM572() {
		return m572;
	}

	public void setM572(Double m572) {
		this.m572 = m572;
	}

	public Double getM573() {
		return m573;
	}

	public void setM573(Double m573) {
		this.m573 = m573;
	}

	public Double getM574() {
		return m574;
	}

	public void setM574(Double m574) {
		this.m574 = m574;
	}

	public Double getM575() {
		return m575;
	}

	public void setM575(Double m575) {
		this.m575 = m575;
	}

	public Double getM576() {
		return m576;
	}

	public void setM576(Double m576) {
		this.m576 = m576;
	}

	public Double getM577() {
		return m577;
	}

	public void setM577(Double m577) {
		this.m577 = m577;
	}

	public Double getM578() {
		return m578;
	}

	public void setM578(Double m578) {
		this.m578 = m578;
	}

	public Double getM579() {
		return m579;
	}

	public void setM579(Double m579) {
		this.m579 = m579;
	}

	public Double getM580() {
		return m580;
	}

	public void setM580(Double m580) {
		this.m580 = m580;
	}

	public Double getM581() {
		return m581;
	}

	public void setM581(Double m581) {
		this.m581 = m581;
	}

	public Double getM582() {
		return m582;
	}

	public void setM582(Double m582) {
		this.m582 = m582;
	}

	public Double getM583() {
		return m583;
	}

	public void setM583(Double m583) {
		this.m583 = m583;
	}

	public Double getM584() {
		return m584;
	}

	public void setM584(Double m584) {
		this.m584 = m584;
	}

	public Double getM585() {
		return m585;
	}

	public void setM585(Double m585) {
		this.m585 = m585;
	}

	public Double getM586() {
		return m586;
	}

	public void setM586(Double m586) {
		this.m586 = m586;
	}

	public Double getM587() {
		return m587;
	}

	public void setM587(Double m587) {
		this.m587 = m587;
	}

	public Double getM588() {
		return m588;
	}

	public void setM588(Double m588) {
		this.m588 = m588;
	}

	public Double getM589() {
		return m589;
	}

	public void setM589(Double m589) {
		this.m589 = m589;
	}

	public Double getM590() {
		return m590;
	}

	public void setM590(Double m590) {
		this.m590 = m590;
	}

	public Double getM591() {
		return m591;
	}

	public void setM591(Double m591) {
		this.m591 = m591;
	}

	public Double getM592() {
		return m592;
	}

	public void setM592(Double m592) {
		this.m592 = m592;
	}

	public Double getM593() {
		return m593;
	}

	public void setM593(Double m593) {
		this.m593 = m593;
	}

	public Double getM594() {
		return m594;
	}

	public void setM594(Double m594) {
		this.m594 = m594;
	}

	public Double getM595() {
		return m595;
	}

	public void setM595(Double m595) {
		this.m595 = m595;
	}

	public Double getM596() {
		return m596;
	}

	public void setM596(Double m596) {
		this.m596 = m596;
	}

	public Double getM597() {
		return m597;
	}

	public void setM597(Double m597) {
		this.m597 = m597;
	}

	public Double getM598() {
		return m598;
	}

	public void setM598(Double m598) {
		this.m598 = m598;
	}

	public Double getM599() {
		return m599;
	}

	public void setM599(Double m599) {
		this.m599 = m599;
	}

	public Double getM600() {
		return m600;
	}

	public void setM600(Double m600) {
		this.m600 = m600;
	}

	public Double getM601() {
		return m601;
	}

	public void setM601(Double m601) {
		this.m601 = m601;
	}

	public Double getM602() {
		return m602;
	}

	public void setM602(Double m602) {
		this.m602 = m602;
	}

	public Double getM603() {
		return m603;
	}

	public void setM603(Double m603) {
		this.m603 = m603;
	}

	public Double getM604() {
		return m604;
	}

	public void setM604(Double m604) {
		this.m604 = m604;
	}

	public Double getM605() {
		return m605;
	}

	public void setM605(Double m605) {
		this.m605 = m605;
	}

	public Double getM606() {
		return m606;
	}

	public void setM606(Double m606) {
		this.m606 = m606;
	}

	public Double getM607() {
		return m607;
	}

	public void setM607(Double m607) {
		this.m607 = m607;
	}

	public Double getM608() {
		return m608;
	}

	public void setM608(Double m608) {
		this.m608 = m608;
	}

	public Double getM609() {
		return m609;
	}

	public void setM609(Double m609) {
		this.m609 = m609;
	}

	public Double getM610() {
		return m610;
	}

	public void setM610(Double m610) {
		this.m610 = m610;
	}

	public Double getM611() {
		return m611;
	}

	public void setM611(Double m611) {
		this.m611 = m611;
	}

	public Double getM612() {
		return m612;
	}

	public void setM612(Double m612) {
		this.m612 = m612;
	}

	public Double getM613() {
		return m613;
	}

	public void setM613(Double m613) {
		this.m613 = m613;
	}

	public Double getM614() {
		return m614;
	}

	public void setM614(Double m614) {
		this.m614 = m614;
	}

	public Double getM615() {
		return m615;
	}

	public void setM615(Double m615) {
		this.m615 = m615;
	}

	public Double getM616() {
		return m616;
	}

	public void setM616(Double m616) {
		this.m616 = m616;
	}

	public Double getM617() {
		return m617;
	}

	public void setM617(Double m617) {
		this.m617 = m617;
	}

	public Double getM618() {
		return m618;
	}

	public void setM618(Double m618) {
		this.m618 = m618;
	}

	public Double getM619() {
		return m619;
	}

	public void setM619(Double m619) {
		this.m619 = m619;
	}

	public Double getM620() {
		return m620;
	}

	public void setM620(Double m620) {
		this.m620 = m620;
	}

	public Double getM621() {
		return m621;
	}

	public void setM621(Double m621) {
		this.m621 = m621;
	}

	public Double getM622() {
		return m622;
	}

	public void setM622(Double m622) {
		this.m622 = m622;
	}

	public Double getM623() {
		return m623;
	}

	public void setM623(Double m623) {
		this.m623 = m623;
	}

	public Double getM624() {
		return m624;
	}

	public void setM624(Double m624) {
		this.m624 = m624;
	}

	public Double getM625() {
		return m625;
	}

	public void setM625(Double m625) {
		this.m625 = m625;
	}

	public Double getM626() {
		return m626;
	}

	public void setM626(Double m626) {
		this.m626 = m626;
	}

	public Double getM627() {
		return m627;
	}

	public void setM627(Double m627) {
		this.m627 = m627;
	}

	public Double getM628() {
		return m628;
	}

	public void setM628(Double m628) {
		this.m628 = m628;
	}

	public Double getM629() {
		return m629;
	}

	public void setM629(Double m629) {
		this.m629 = m629;
	}

	public Double getM630() {
		return m630;
	}

	public void setM630(Double m630) {
		this.m630 = m630;
	}

	public Double getM631() {
		return m631;
	}

	public void setM631(Double m631) {
		this.m631 = m631;
	}

	public Double getM632() {
		return m632;
	}

	public void setM632(Double m632) {
		this.m632 = m632;
	}

	public Double getM633() {
		return m633;
	}

	public void setM633(Double m633) {
		this.m633 = m633;
	}

	public Double getM634() {
		return m634;
	}

	public void setM634(Double m634) {
		this.m634 = m634;
	}

	public Double getM635() {
		return m635;
	}

	public void setM635(Double m635) {
		this.m635 = m635;
	}

	public Double getM636() {
		return m636;
	}

	public void setM636(Double m636) {
		this.m636 = m636;
	}

	public Double getM637() {
		return m637;
	}

	public void setM637(Double m637) {
		this.m637 = m637;
	}

	public Double getM638() {
		return m638;
	}

	public void setM638(Double m638) {
		this.m638 = m638;
	}

	public Double getM639() {
		return m639;
	}

	public void setM639(Double m639) {
		this.m639 = m639;
	}

	public Double getM640() {
		return m640;
	}

	public void setM640(Double m640) {
		this.m640 = m640;
	}

	public Double getM641() {
		return m641;
	}

	public void setM641(Double m641) {
		this.m641 = m641;
	}

	public Double getM642() {
		return m642;
	}

	public void setM642(Double m642) {
		this.m642 = m642;
	}

	public Double getM643() {
		return m643;
	}

	public void setM643(Double m643) {
		this.m643 = m643;
	}

	public Double getM644() {
		return m644;
	}

	public void setM644(Double m644) {
		this.m644 = m644;
	}

	public Double getM645() {
		return m645;
	}

	public void setM645(Double m645) {
		this.m645 = m645;
	}

	public Double getM646() {
		return m646;
	}

	public void setM646(Double m646) {
		this.m646 = m646;
	}

	public Double getM647() {
		return m647;
	}

	public void setM647(Double m647) {
		this.m647 = m647;
	}

	public Double getM648() {
		return m648;
	}

	public void setM648(Double m648) {
		this.m648 = m648;
	}

	public Double getM649() {
		return m649;
	}

	public void setM649(Double m649) {
		this.m649 = m649;
	}

	public Double getM650() {
		return m650;
	}

	public void setM650(Double m650) {
		this.m650 = m650;
	}

	public Double getM651() {
		return m651;
	}

	public void setM651(Double m651) {
		this.m651 = m651;
	}

	public Double getM652() {
		return m652;
	}

	public void setM652(Double m652) {
		this.m652 = m652;
	}

	public Double getM653() {
		return m653;
	}

	public void setM653(Double m653) {
		this.m653 = m653;
	}

	public Double getM654() {
		return m654;
	}

	public void setM654(Double m654) {
		this.m654 = m654;
	}

	public Double getM655() {
		return m655;
	}

	public void setM655(Double m655) {
		this.m655 = m655;
	}

	public Double getM656() {
		return m656;
	}

	public void setM656(Double m656) {
		this.m656 = m656;
	}

	public Double getM657() {
		return m657;
	}

	public void setM657(Double m657) {
		this.m657 = m657;
	}

	public Double getM658() {
		return m658;
	}

	public void setM658(Double m658) {
		this.m658 = m658;
	}

	public Double getM659() {
		return m659;
	}

	public void setM659(Double m659) {
		this.m659 = m659;
	}

	public Double getM660() {
		return m660;
	}

	public void setM660(Double m660) {
		this.m660 = m660;
	}

	public Double getM661() {
		return m661;
	}

	public void setM661(Double m661) {
		this.m661 = m661;
	}

	public Double getM662() {
		return m662;
	}

	public void setM662(Double m662) {
		this.m662 = m662;
	}

	public Double getM663() {
		return m663;
	}

	public void setM663(Double m663) {
		this.m663 = m663;
	}

	public Double getM664() {
		return m664;
	}

	public void setM664(Double m664) {
		this.m664 = m664;
	}

	public Double getM665() {
		return m665;
	}

	public void setM665(Double m665) {
		this.m665 = m665;
	}

	public Double getM666() {
		return m666;
	}

	public void setM666(Double m666) {
		this.m666 = m666;
	}

	public Double getM667() {
		return m667;
	}

	public void setM667(Double m667) {
		this.m667 = m667;
	}

	public Double getM668() {
		return m668;
	}

	public void setM668(Double m668) {
		this.m668 = m668;
	}

	public Double getM669() {
		return m669;
	}

	public void setM669(Double m669) {
		this.m669 = m669;
	}

	public Double getM670() {
		return m670;
	}

	public void setM670(Double m670) {
		this.m670 = m670;
	}

	public Double getM671() {
		return m671;
	}

	public void setM671(Double m671) {
		this.m671 = m671;
	}

	public Double getM672() {
		return m672;
	}

	public void setM672(Double m672) {
		this.m672 = m672;
	}

	public Double getM673() {
		return m673;
	}

	public void setM673(Double m673) {
		this.m673 = m673;
	}

	public Double getM674() {
		return m674;
	}

	public void setM674(Double m674) {
		this.m674 = m674;
	}

	public Double getM675() {
		return m675;
	}

	public void setM675(Double m675) {
		this.m675 = m675;
	}

	public Double getM676() {
		return m676;
	}

	public void setM676(Double m676) {
		this.m676 = m676;
	}

	public Double getM677() {
		return m677;
	}

	public void setM677(Double m677) {
		this.m677 = m677;
	}

	public Double getM678() {
		return m678;
	}

	public void setM678(Double m678) {
		this.m678 = m678;
	}

	public Double getM679() {
		return m679;
	}

	public void setM679(Double m679) {
		this.m679 = m679;
	}

	public Double getM680() {
		return m680;
	}

	public void setM680(Double m680) {
		this.m680 = m680;
	}

	public Double getM681() {
		return m681;
	}

	public void setM681(Double m681) {
		this.m681 = m681;
	}

	public Double getM682() {
		return m682;
	}

	public void setM682(Double m682) {
		this.m682 = m682;
	}

	public Double getM683() {
		return m683;
	}

	public void setM683(Double m683) {
		this.m683 = m683;
	}

	public Double getM684() {
		return m684;
	}

	public void setM684(Double m684) {
		this.m684 = m684;
	}

	public Double getM685() {
		return m685;
	}

	public void setM685(Double m685) {
		this.m685 = m685;
	}

	public Double getM686() {
		return m686;
	}

	public void setM686(Double m686) {
		this.m686 = m686;
	}

	public Double getM687() {
		return m687;
	}

	public void setM687(Double m687) {
		this.m687 = m687;
	}

	public Double getM688() {
		return m688;
	}

	public void setM688(Double m688) {
		this.m688 = m688;
	}

	public Double getM689() {
		return m689;
	}

	public void setM689(Double m689) {
		this.m689 = m689;
	}

	public Double getM690() {
		return m690;
	}

	public void setM690(Double m690) {
		this.m690 = m690;
	}

	public Double getM691() {
		return m691;
	}

	public void setM691(Double m691) {
		this.m691 = m691;
	}

	public Double getM692() {
		return m692;
	}

	public void setM692(Double m692) {
		this.m692 = m692;
	}

	public Double getM693() {
		return m693;
	}

	public void setM693(Double m693) {
		this.m693 = m693;
	}

	public Double getM694() {
		return m694;
	}

	public void setM694(Double m694) {
		this.m694 = m694;
	}

	public Double getM695() {
		return m695;
	}

	public void setM695(Double m695) {
		this.m695 = m695;
	}

	public Double getM696() {
		return m696;
	}

	public void setM696(Double m696) {
		this.m696 = m696;
	}

	public Double getM697() {
		return m697;
	}

	public void setM697(Double m697) {
		this.m697 = m697;
	}

	public Double getM698() {
		return m698;
	}

	public void setM698(Double m698) {
		this.m698 = m698;
	}

	public Double getM699() {
		return m699;
	}

	public void setM699(Double m699) {
		this.m699 = m699;
	}

	public Double getM700() {
		return m700;
	}

	public void setM700(Double m700) {
		this.m700 = m700;
	}

	public Double getM701() {
		return m701;
	}

	public void setM701(Double m701) {
		this.m701 = m701;
	}

	public Double getM702() {
		return m702;
	}

	public void setM702(Double m702) {
		this.m702 = m702;
	}

	public Double getM703() {
		return m703;
	}

	public void setM703(Double m703) {
		this.m703 = m703;
	}

	public Double getM704() {
		return m704;
	}

	public void setM704(Double m704) {
		this.m704 = m704;
	}

	public Double getM705() {
		return m705;
	}

	public void setM705(Double m705) {
		this.m705 = m705;
	}

	public Double getM706() {
		return m706;
	}

	public void setM706(Double m706) {
		this.m706 = m706;
	}

	public Double getM707() {
		return m707;
	}

	public void setM707(Double m707) {
		this.m707 = m707;
	}

	public Double getM708() {
		return m708;
	}

	public void setM708(Double m708) {
		this.m708 = m708;
	}

	public Double getM709() {
		return m709;
	}

	public void setM709(Double m709) {
		this.m709 = m709;
	}

	public Double getM710() {
		return m710;
	}

	public void setM710(Double m710) {
		this.m710 = m710;
	}

	public Double getM711() {
		return m711;
	}

	public void setM711(Double m711) {
		this.m711 = m711;
	}

	public Double getM712() {
		return m712;
	}

	public void setM712(Double m712) {
		this.m712 = m712;
	}

	public Double getM713() {
		return m713;
	}

	public void setM713(Double m713) {
		this.m713 = m713;
	}

	public Double getM714() {
		return m714;
	}

	public void setM714(Double m714) {
		this.m714 = m714;
	}

	public Double getM715() {
		return m715;
	}

	public void setM715(Double m715) {
		this.m715 = m715;
	}

	public Double getM716() {
		return m716;
	}

	public void setM716(Double m716) {
		this.m716 = m716;
	}

	public Double getM717() {
		return m717;
	}

	public void setM717(Double m717) {
		this.m717 = m717;
	}

	public Double getM718() {
		return m718;
	}

	public void setM718(Double m718) {
		this.m718 = m718;
	}

	public Double getM719() {
		return m719;
	}

	public void setM719(Double m719) {
		this.m719 = m719;
	}

	public Double getM720() {
		return m720;
	}

	public void setM720(Double m720) {
		this.m720 = m720;
	}

	public Double getM721() {
		return m721;
	}

	public void setM721(Double m721) {
		this.m721 = m721;
	}

	public Double getM722() {
		return m722;
	}

	public void setM722(Double m722) {
		this.m722 = m722;
	}

	public Double getM723() {
		return m723;
	}

	public void setM723(Double m723) {
		this.m723 = m723;
	}

	public Double getM724() {
		return m724;
	}

	public void setM724(Double m724) {
		this.m724 = m724;
	}

	public Double getM725() {
		return m725;
	}

	public void setM725(Double m725) {
		this.m725 = m725;
	}

	public Double getM726() {
		return m726;
	}

	public void setM726(Double m726) {
		this.m726 = m726;
	}

	public Double getM727() {
		return m727;
	}

	public void setM727(Double m727) {
		this.m727 = m727;
	}

	public Double getM728() {
		return m728;
	}

	public void setM728(Double m728) {
		this.m728 = m728;
	}

	public Double getM729() {
		return m729;
	}

	public void setM729(Double m729) {
		this.m729 = m729;
	}

	public Double getM730() {
		return m730;
	}

	public void setM730(Double m730) {
		this.m730 = m730;
	}

	public Double getM731() {
		return m731;
	}

	public void setM731(Double m731) {
		this.m731 = m731;
	}

	public Double getM732() {
		return m732;
	}

	public void setM732(Double m732) {
		this.m732 = m732;
	}

	public Double getM733() {
		return m733;
	}

	public void setM733(Double m733) {
		this.m733 = m733;
	}

	public Double getM734() {
		return m734;
	}

	public void setM734(Double m734) {
		this.m734 = m734;
	}

	public Double getM735() {
		return m735;
	}

	public void setM735(Double m735) {
		this.m735 = m735;
	}

	public Double getM736() {
		return m736;
	}

	public void setM736(Double m736) {
		this.m736 = m736;
	}

	public Double getM737() {
		return m737;
	}

	public void setM737(Double m737) {
		this.m737 = m737;
	}

	public Double getM738() {
		return m738;
	}

	public void setM738(Double m738) {
		this.m738 = m738;
	}

	public Double getM739() {
		return m739;
	}

	public void setM739(Double m739) {
		this.m739 = m739;
	}

	public Double getM740() {
		return m740;
	}

	public void setM740(Double m740) {
		this.m740 = m740;
	}

	public Double getM741() {
		return m741;
	}

	public void setM741(Double m741) {
		this.m741 = m741;
	}

	public Double getM742() {
		return m742;
	}

	public void setM742(Double m742) {
		this.m742 = m742;
	}

	public Double getM743() {
		return m743;
	}

	public void setM743(Double m743) {
		this.m743 = m743;
	}

	public Double getM744() {
		return m744;
	}

	public void setM744(Double m744) {
		this.m744 = m744;
	}

	public Double getM745() {
		return m745;
	}

	public void setM745(Double m745) {
		this.m745 = m745;
	}

	public Double getM746() {
		return m746;
	}

	public void setM746(Double m746) {
		this.m746 = m746;
	}

	public Double getM747() {
		return m747;
	}

	public void setM747(Double m747) {
		this.m747 = m747;
	}

	public Double getM748() {
		return m748;
	}

	public void setM748(Double m748) {
		this.m748 = m748;
	}

	public Double getM749() {
		return m749;
	}

	public void setM749(Double m749) {
		this.m749 = m749;
	}

	public Double getM750() {
		return m750;
	}

	public void setM750(Double m750) {
		this.m750 = m750;
	}

	public Double getM751() {
		return m751;
	}

	public void setM751(Double m751) {
		this.m751 = m751;
	}

	public Double getM752() {
		return m752;
	}

	public void setM752(Double m752) {
		this.m752 = m752;
	}

	public Double getM753() {
		return m753;
	}

	public void setM753(Double m753) {
		this.m753 = m753;
	}

	public Double getM754() {
		return m754;
	}

	public void setM754(Double m754) {
		this.m754 = m754;
	}

	public Double getM755() {
		return m755;
	}

	public void setM755(Double m755) {
		this.m755 = m755;
	}

	public Double getM756() {
		return m756;
	}

	public void setM756(Double m756) {
		this.m756 = m756;
	}

	public Double getM757() {
		return m757;
	}

	public void setM757(Double m757) {
		this.m757 = m757;
	}

	public Double getM758() {
		return m758;
	}

	public void setM758(Double m758) {
		this.m758 = m758;
	}

	public Double getM759() {
		return m759;
	}

	public void setM759(Double m759) {
		this.m759 = m759;
	}

	public Double getM760() {
		return m760;
	}

	public void setM760(Double m760) {
		this.m760 = m760;
	}

	public Double getM761() {
		return m761;
	}

	public void setM761(Double m761) {
		this.m761 = m761;
	}

	public Double getM762() {
		return m762;
	}

	public void setM762(Double m762) {
		this.m762 = m762;
	}

	public Double getM763() {
		return m763;
	}

	public void setM763(Double m763) {
		this.m763 = m763;
	}

	public Double getM764() {
		return m764;
	}

	public void setM764(Double m764) {
		this.m764 = m764;
	}

	public Double getM765() {
		return m765;
	}

	public void setM765(Double m765) {
		this.m765 = m765;
	}

	public Double getM766() {
		return m766;
	}

	public void setM766(Double m766) {
		this.m766 = m766;
	}

	public Double getM767() {
		return m767;
	}

	public void setM767(Double m767) {
		this.m767 = m767;
	}

	public Double getM768() {
		return m768;
	}

	public void setM768(Double m768) {
		this.m768 = m768;
	}

	public Double getM769() {
		return m769;
	}

	public void setM769(Double m769) {
		this.m769 = m769;
	}

	public Double getM770() {
		return m770;
	}

	public void setM770(Double m770) {
		this.m770 = m770;
	}

	public Double getM771() {
		return m771;
	}

	public void setM771(Double m771) {
		this.m771 = m771;
	}

	public Double getM772() {
		return m772;
	}

	public void setM772(Double m772) {
		this.m772 = m772;
	}

	public Double getM773() {
		return m773;
	}

	public void setM773(Double m773) {
		this.m773 = m773;
	}

	public Double getM774() {
		return m774;
	}

	public void setM774(Double m774) {
		this.m774 = m774;
	}

	public Double getM775() {
		return m775;
	}

	public void setM775(Double m775) {
		this.m775 = m775;
	}

	public Double getM776() {
		return m776;
	}

	public void setM776(Double m776) {
		this.m776 = m776;
	}

	public Double getM777() {
		return m777;
	}

	public void setM777(Double m777) {
		this.m777 = m777;
	}

	public Double getM778() {
		return m778;
	}

	public void setM778(Double m778) {
		this.m778 = m778;
	}

	public Double getM779() {
		return m779;
	}

	public void setM779(Double m779) {
		this.m779 = m779;
	}

	public Double getM780() {
		return m780;
	}

	public void setM780(Double m780) {
		this.m780 = m780;
	}

	public Double getM781() {
		return m781;
	}

	public void setM781(Double m781) {
		this.m781 = m781;
	}

	public Double getM782() {
		return m782;
	}

	public void setM782(Double m782) {
		this.m782 = m782;
	}

	public Double getM783() {
		return m783;
	}

	public void setM783(Double m783) {
		this.m783 = m783;
	}

	public Double getM784() {
		return m784;
	}

	public void setM784(Double m784) {
		this.m784 = m784;
	}

	public Double getM785() {
		return m785;
	}

	public void setM785(Double m785) {
		this.m785 = m785;
	}

	public Double getM786() {
		return m786;
	}

	public void setM786(Double m786) {
		this.m786 = m786;
	}

	public Double getM787() {
		return m787;
	}

	public void setM787(Double m787) {
		this.m787 = m787;
	}

	public Double getM788() {
		return m788;
	}

	public void setM788(Double m788) {
		this.m788 = m788;
	}

	public Double getM789() {
		return m789;
	}

	public void setM789(Double m789) {
		this.m789 = m789;
	}

	public Double getM790() {
		return m790;
	}

	public void setM790(Double m790) {
		this.m790 = m790;
	}

	public Double getM791() {
		return m791;
	}

	public void setM791(Double m791) {
		this.m791 = m791;
	}

	public Double getM792() {
		return m792;
	}

	public void setM792(Double m792) {
		this.m792 = m792;
	}

	public Double getM793() {
		return m793;
	}

	public void setM793(Double m793) {
		this.m793 = m793;
	}

	public Double getM794() {
		return m794;
	}

	public void setM794(Double m794) {
		this.m794 = m794;
	}

	public Double getM795() {
		return m795;
	}

	public void setM795(Double m795) {
		this.m795 = m795;
	}

	public Double getM796() {
		return m796;
	}

	public void setM796(Double m796) {
		this.m796 = m796;
	}

	public Double getM797() {
		return m797;
	}

	public void setM797(Double m797) {
		this.m797 = m797;
	}

	public Double getM798() {
		return m798;
	}

	public void setM798(Double m798) {
		this.m798 = m798;
	}

	public Double getM799() {
		return m799;
	}

	public void setM799(Double m799) {
		this.m799 = m799;
	}

	public Double getM800() {
		return m800;
	}

	public void setM800(Double m800) {
		this.m800 = m800;
	}

	public Double getM801() {
		return m801;
	}

	public void setM801(Double m801) {
		this.m801 = m801;
	}

	public Double getM802() {
		return m802;
	}

	public void setM802(Double m802) {
		this.m802 = m802;
	}

	public Double getM803() {
		return m803;
	}

	public void setM803(Double m803) {
		this.m803 = m803;
	}

	public Double getM804() {
		return m804;
	}

	public void setM804(Double m804) {
		this.m804 = m804;
	}

	public Double getM805() {
		return m805;
	}

	public void setM805(Double m805) {
		this.m805 = m805;
	}

	public Double getM806() {
		return m806;
	}

	public void setM806(Double m806) {
		this.m806 = m806;
	}

	public Double getM807() {
		return m807;
	}

	public void setM807(Double m807) {
		this.m807 = m807;
	}

	public Double getM808() {
		return m808;
	}

	public void setM808(Double m808) {
		this.m808 = m808;
	}

	public Double getM809() {
		return m809;
	}

	public void setM809(Double m809) {
		this.m809 = m809;
	}

	public Double getM810() {
		return m810;
	}

	public void setM810(Double m810) {
		this.m810 = m810;
	}

	public Double getM811() {
		return m811;
	}

	public void setM811(Double m811) {
		this.m811 = m811;
	}

	public Double getM812() {
		return m812;
	}

	public void setM812(Double m812) {
		this.m812 = m812;
	}

	public Double getM813() {
		return m813;
	}

	public void setM813(Double m813) {
		this.m813 = m813;
	}

	public Double getM814() {
		return m814;
	}

	public void setM814(Double m814) {
		this.m814 = m814;
	}

	public Double getM815() {
		return m815;
	}

	public void setM815(Double m815) {
		this.m815 = m815;
	}

	public Double getM816() {
		return m816;
	}

	public void setM816(Double m816) {
		this.m816 = m816;
	}

	public Double getM817() {
		return m817;
	}

	public void setM817(Double m817) {
		this.m817 = m817;
	}

	public Double getM818() {
		return m818;
	}

	public void setM818(Double m818) {
		this.m818 = m818;
	}

	public Double getM819() {
		return m819;
	}

	public void setM819(Double m819) {
		this.m819 = m819;
	}

	public Double getM820() {
		return m820;
	}

	public void setM820(Double m820) {
		this.m820 = m820;
	}

	public Double getM821() {
		return m821;
	}

	public void setM821(Double m821) {
		this.m821 = m821;
	}

	public Double getM822() {
		return m822;
	}

	public void setM822(Double m822) {
		this.m822 = m822;
	}

	public Double getM823() {
		return m823;
	}

	public void setM823(Double m823) {
		this.m823 = m823;
	}

	public Double getM824() {
		return m824;
	}

	public void setM824(Double m824) {
		this.m824 = m824;
	}

	public Double getM825() {
		return m825;
	}

	public void setM825(Double m825) {
		this.m825 = m825;
	}

	public Double getM826() {
		return m826;
	}

	public void setM826(Double m826) {
		this.m826 = m826;
	}

	public Double getM827() {
		return m827;
	}

	public void setM827(Double m827) {
		this.m827 = m827;
	}

	public Double getM828() {
		return m828;
	}

	public void setM828(Double m828) {
		this.m828 = m828;
	}

	public Double getM829() {
		return m829;
	}

	public void setM829(Double m829) {
		this.m829 = m829;
	}

	public Double getM830() {
		return m830;
	}

	public void setM830(Double m830) {
		this.m830 = m830;
	}

	public Double getM831() {
		return m831;
	}

	public void setM831(Double m831) {
		this.m831 = m831;
	}

	public Double getM832() {
		return m832;
	}

	public void setM832(Double m832) {
		this.m832 = m832;
	}

	public Double getM833() {
		return m833;
	}

	public void setM833(Double m833) {
		this.m833 = m833;
	}

	public Double getM834() {
		return m834;
	}

	public void setM834(Double m834) {
		this.m834 = m834;
	}

	public Double getM835() {
		return m835;
	}

	public void setM835(Double m835) {
		this.m835 = m835;
	}

	public Double getM836() {
		return m836;
	}

	public void setM836(Double m836) {
		this.m836 = m836;
	}

	public Double getM837() {
		return m837;
	}

	public void setM837(Double m837) {
		this.m837 = m837;
	}

	public Double getM838() {
		return m838;
	}

	public void setM838(Double m838) {
		this.m838 = m838;
	}

	public Double getM839() {
		return m839;
	}

	public void setM839(Double m839) {
		this.m839 = m839;
	}

	public Double getM840() {
		return m840;
	}

	public void setM840(Double m840) {
		this.m840 = m840;
	}

	public Double getM841() {
		return m841;
	}

	public void setM841(Double m841) {
		this.m841 = m841;
	}

	public Double getM842() {
		return m842;
	}

	public void setM842(Double m842) {
		this.m842 = m842;
	}

	public Double getM843() {
		return m843;
	}

	public void setM843(Double m843) {
		this.m843 = m843;
	}

	public Double getM844() {
		return m844;
	}

	public void setM844(Double m844) {
		this.m844 = m844;
	}

	public Double getM845() {
		return m845;
	}

	public void setM845(Double m845) {
		this.m845 = m845;
	}

	public Double getM846() {
		return m846;
	}

	public void setM846(Double m846) {
		this.m846 = m846;
	}

	public Double getM847() {
		return m847;
	}

	public void setM847(Double m847) {
		this.m847 = m847;
	}

	public Double getM848() {
		return m848;
	}

	public void setM848(Double m848) {
		this.m848 = m848;
	}

	public Double getM849() {
		return m849;
	}

	public void setM849(Double m849) {
		this.m849 = m849;
	}

	public Double getM850() {
		return m850;
	}

	public void setM850(Double m850) {
		this.m850 = m850;
	}

	public Double getM851() {
		return m851;
	}

	public void setM851(Double m851) {
		this.m851 = m851;
	}

	public Double getM852() {
		return m852;
	}

	public void setM852(Double m852) {
		this.m852 = m852;
	}

	public Double getM853() {
		return m853;
	}

	public void setM853(Double m853) {
		this.m853 = m853;
	}

	public Double getM854() {
		return m854;
	}

	public void setM854(Double m854) {
		this.m854 = m854;
	}

	public Double getM855() {
		return m855;
	}

	public void setM855(Double m855) {
		this.m855 = m855;
	}

	public Double getM856() {
		return m856;
	}

	public void setM856(Double m856) {
		this.m856 = m856;
	}

	public Double getM857() {
		return m857;
	}

	public void setM857(Double m857) {
		this.m857 = m857;
	}

	public Double getM858() {
		return m858;
	}

	public void setM858(Double m858) {
		this.m858 = m858;
	}

	public Double getM859() {
		return m859;
	}

	public void setM859(Double m859) {
		this.m859 = m859;
	}

	public Double getM860() {
		return m860;
	}

	public void setM860(Double m860) {
		this.m860 = m860;
	}

	public Double getM861() {
		return m861;
	}

	public void setM861(Double m861) {
		this.m861 = m861;
	}

	public Double getM862() {
		return m862;
	}

	public void setM862(Double m862) {
		this.m862 = m862;
	}

	public Double getM863() {
		return m863;
	}

	public void setM863(Double m863) {
		this.m863 = m863;
	}

	public Double getM864() {
		return m864;
	}

	public void setM864(Double m864) {
		this.m864 = m864;
	}

	public Double getM865() {
		return m865;
	}

	public void setM865(Double m865) {
		this.m865 = m865;
	}

	public Double getM866() {
		return m866;
	}

	public void setM866(Double m866) {
		this.m866 = m866;
	}

	public Double getM867() {
		return m867;
	}

	public void setM867(Double m867) {
		this.m867 = m867;
	}

	public Double getM868() {
		return m868;
	}

	public void setM868(Double m868) {
		this.m868 = m868;
	}

	public Double getM869() {
		return m869;
	}

	public void setM869(Double m869) {
		this.m869 = m869;
	}

	public Double getM870() {
		return m870;
	}

	public void setM870(Double m870) {
		this.m870 = m870;
	}

	public Double getM871() {
		return m871;
	}

	public void setM871(Double m871) {
		this.m871 = m871;
	}

	public Double getM872() {
		return m872;
	}

	public void setM872(Double m872) {
		this.m872 = m872;
	}

	public Double getM873() {
		return m873;
	}

	public void setM873(Double m873) {
		this.m873 = m873;
	}

	public Double getM874() {
		return m874;
	}

	public void setM874(Double m874) {
		this.m874 = m874;
	}

	public Double getM875() {
		return m875;
	}

	public void setM875(Double m875) {
		this.m875 = m875;
	}

	public Double getM876() {
		return m876;
	}

	public void setM876(Double m876) {
		this.m876 = m876;
	}

	public Double getM877() {
		return m877;
	}

	public void setM877(Double m877) {
		this.m877 = m877;
	}

	public Double getM878() {
		return m878;
	}

	public void setM878(Double m878) {
		this.m878 = m878;
	}

	public Double getM879() {
		return m879;
	}

	public void setM879(Double m879) {
		this.m879 = m879;
	}

	public Double getM880() {
		return m880;
	}

	public void setM880(Double m880) {
		this.m880 = m880;
	}

	public Double getM881() {
		return m881;
	}

	public void setM881(Double m881) {
		this.m881 = m881;
	}

	public Double getM882() {
		return m882;
	}

	public void setM882(Double m882) {
		this.m882 = m882;
	}

	public Double getM883() {
		return m883;
	}

	public void setM883(Double m883) {
		this.m883 = m883;
	}

	public Double getM884() {
		return m884;
	}

	public void setM884(Double m884) {
		this.m884 = m884;
	}

	public Double getM885() {
		return m885;
	}

	public void setM885(Double m885) {
		this.m885 = m885;
	}

	public Double getM886() {
		return m886;
	}

	public void setM886(Double m886) {
		this.m886 = m886;
	}

	public Double getM887() {
		return m887;
	}

	public void setM887(Double m887) {
		this.m887 = m887;
	}

	public Double getM888() {
		return m888;
	}

	public void setM888(Double m888) {
		this.m888 = m888;
	}

	public Double getM889() {
		return m889;
	}

	public void setM889(Double m889) {
		this.m889 = m889;
	}

	public Double getM890() {
		return m890;
	}

	public void setM890(Double m890) {
		this.m890 = m890;
	}

	public Double getM891() {
		return m891;
	}

	public void setM891(Double m891) {
		this.m891 = m891;
	}

	public Double getM892() {
		return m892;
	}

	public void setM892(Double m892) {
		this.m892 = m892;
	}

	public Double getM893() {
		return m893;
	}

	public void setM893(Double m893) {
		this.m893 = m893;
	}

	public Double getM894() {
		return m894;
	}

	public void setM894(Double m894) {
		this.m894 = m894;
	}

	public Double getM895() {
		return m895;
	}

	public void setM895(Double m895) {
		this.m895 = m895;
	}

	public Double getM896() {
		return m896;
	}

	public void setM896(Double m896) {
		this.m896 = m896;
	}

	public Double getM897() {
		return m897;
	}

	public void setM897(Double m897) {
		this.m897 = m897;
	}

	public Double getM898() {
		return m898;
	}

	public void setM898(Double m898) {
		this.m898 = m898;
	}

	public Double getM899() {
		return m899;
	}

	public void setM899(Double m899) {
		this.m899 = m899;
	}

	public Double getM900() {
		return m900;
	}

	public void setM900(Double m900) {
		this.m900 = m900;
	}

	public Double getM901() {
		return m901;
	}

	public void setM901(Double m901) {
		this.m901 = m901;
	}

	public Double getM902() {
		return m902;
	}

	public void setM902(Double m902) {
		this.m902 = m902;
	}

	public Double getM903() {
		return m903;
	}

	public void setM903(Double m903) {
		this.m903 = m903;
	}

	public Double getM904() {
		return m904;
	}

	public void setM904(Double m904) {
		this.m904 = m904;
	}

	public Double getM905() {
		return m905;
	}

	public void setM905(Double m905) {
		this.m905 = m905;
	}

	public Double getM906() {
		return m906;
	}

	public void setM906(Double m906) {
		this.m906 = m906;
	}

	public Double getM907() {
		return m907;
	}

	public void setM907(Double m907) {
		this.m907 = m907;
	}

	public Double getM908() {
		return m908;
	}

	public void setM908(Double m908) {
		this.m908 = m908;
	}

	public Double getM909() {
		return m909;
	}

	public void setM909(Double m909) {
		this.m909 = m909;
	}

	public Double getM910() {
		return m910;
	}

	public void setM910(Double m910) {
		this.m910 = m910;
	}

	public Double getM911() {
		return m911;
	}

	public void setM911(Double m911) {
		this.m911 = m911;
	}

	public Double getM912() {
		return m912;
	}

	public void setM912(Double m912) {
		this.m912 = m912;
	}

	public Double getM913() {
		return m913;
	}

	public void setM913(Double m913) {
		this.m913 = m913;
	}

	public Double getM914() {
		return m914;
	}

	public void setM914(Double m914) {
		this.m914 = m914;
	}

	public Double getM915() {
		return m915;
	}

	public void setM915(Double m915) {
		this.m915 = m915;
	}

	public Double getM916() {
		return m916;
	}

	public void setM916(Double m916) {
		this.m916 = m916;
	}

	public Double getM917() {
		return m917;
	}

	public void setM917(Double m917) {
		this.m917 = m917;
	}

	public Double getM918() {
		return m918;
	}

	public void setM918(Double m918) {
		this.m918 = m918;
	}

	public Double getM919() {
		return m919;
	}

	public void setM919(Double m919) {
		this.m919 = m919;
	}

	public Double getM920() {
		return m920;
	}

	public void setM920(Double m920) {
		this.m920 = m920;
	}

	public Double getM921() {
		return m921;
	}

	public void setM921(Double m921) {
		this.m921 = m921;
	}

	public Double getM922() {
		return m922;
	}

	public void setM922(Double m922) {
		this.m922 = m922;
	}

	public Double getM923() {
		return m923;
	}

	public void setM923(Double m923) {
		this.m923 = m923;
	}

	public Double getM924() {
		return m924;
	}

	public void setM924(Double m924) {
		this.m924 = m924;
	}

	public Double getM925() {
		return m925;
	}

	public void setM925(Double m925) {
		this.m925 = m925;
	}

	public Double getM926() {
		return m926;
	}

	public void setM926(Double m926) {
		this.m926 = m926;
	}

	public Double getM927() {
		return m927;
	}

	public void setM927(Double m927) {
		this.m927 = m927;
	}

	public Double getM928() {
		return m928;
	}

	public void setM928(Double m928) {
		this.m928 = m928;
	}

	public Double getM929() {
		return m929;
	}

	public void setM929(Double m929) {
		this.m929 = m929;
	}

	public Double getM930() {
		return m930;
	}

	public void setM930(Double m930) {
		this.m930 = m930;
	}

	public Double getM931() {
		return m931;
	}

	public void setM931(Double m931) {
		this.m931 = m931;
	}

	public Double getM932() {
		return m932;
	}

	public void setM932(Double m932) {
		this.m932 = m932;
	}

	public Double getM933() {
		return m933;
	}

	public void setM933(Double m933) {
		this.m933 = m933;
	}

	public Double getM934() {
		return m934;
	}

	public void setM934(Double m934) {
		this.m934 = m934;
	}

	public Double getM935() {
		return m935;
	}

	public void setM935(Double m935) {
		this.m935 = m935;
	}

	public Double getM936() {
		return m936;
	}

	public void setM936(Double m936) {
		this.m936 = m936;
	}

	public Double getM937() {
		return m937;
	}

	public void setM937(Double m937) {
		this.m937 = m937;
	}

	public Double getM938() {
		return m938;
	}

	public void setM938(Double m938) {
		this.m938 = m938;
	}

	public Double getM939() {
		return m939;
	}

	public void setM939(Double m939) {
		this.m939 = m939;
	}

	public Double getM940() {
		return m940;
	}

	public void setM940(Double m940) {
		this.m940 = m940;
	}

	public Double getM941() {
		return m941;
	}

	public void setM941(Double m941) {
		this.m941 = m941;
	}

	public Double getM942() {
		return m942;
	}

	public void setM942(Double m942) {
		this.m942 = m942;
	}

	public Double getM943() {
		return m943;
	}

	public void setM943(Double m943) {
		this.m943 = m943;
	}

	public Double getM944() {
		return m944;
	}

	public void setM944(Double m944) {
		this.m944 = m944;
	}

	public Double getM945() {
		return m945;
	}

	public void setM945(Double m945) {
		this.m945 = m945;
	}

	public Double getM946() {
		return m946;
	}

	public void setM946(Double m946) {
		this.m946 = m946;
	}

	public Double getM947() {
		return m947;
	}

	public void setM947(Double m947) {
		this.m947 = m947;
	}

	public Double getM948() {
		return m948;
	}

	public void setM948(Double m948) {
		this.m948 = m948;
	}

	public Double getM949() {
		return m949;
	}

	public void setM949(Double m949) {
		this.m949 = m949;
	}

	public Double getM950() {
		return m950;
	}

	public void setM950(Double m950) {
		this.m950 = m950;
	}

	public Double getM951() {
		return m951;
	}

	public void setM951(Double m951) {
		this.m951 = m951;
	}

	public Double getM952() {
		return m952;
	}

	public void setM952(Double m952) {
		this.m952 = m952;
	}

	public Double getM953() {
		return m953;
	}

	public void setM953(Double m953) {
		this.m953 = m953;
	}

	public Double getM954() {
		return m954;
	}

	public void setM954(Double m954) {
		this.m954 = m954;
	}

	public Double getM955() {
		return m955;
	}

	public void setM955(Double m955) {
		this.m955 = m955;
	}

	public Double getM956() {
		return m956;
	}

	public void setM956(Double m956) {
		this.m956 = m956;
	}

	public Double getM957() {
		return m957;
	}

	public void setM957(Double m957) {
		this.m957 = m957;
	}

	public Double getM958() {
		return m958;
	}

	public void setM958(Double m958) {
		this.m958 = m958;
	}

	public Double getM959() {
		return m959;
	}

	public void setM959(Double m959) {
		this.m959 = m959;
	}

	public Double getM960() {
		return m960;
	}

	public void setM960(Double m960) {
		this.m960 = m960;
	}

	public Double getM961() {
		return m961;
	}

	public void setM961(Double m961) {
		this.m961 = m961;
	}

	public Double getM962() {
		return m962;
	}

	public void setM962(Double m962) {
		this.m962 = m962;
	}

	public Double getM963() {
		return m963;
	}

	public void setM963(Double m963) {
		this.m963 = m963;
	}

	public Double getM964() {
		return m964;
	}

	public void setM964(Double m964) {
		this.m964 = m964;
	}

	public Double getM965() {
		return m965;
	}

	public void setM965(Double m965) {
		this.m965 = m965;
	}

	public Double getM966() {
		return m966;
	}

	public void setM966(Double m966) {
		this.m966 = m966;
	}

	public Double getM967() {
		return m967;
	}

	public void setM967(Double m967) {
		this.m967 = m967;
	}

	public Double getM968() {
		return m968;
	}

	public void setM968(Double m968) {
		this.m968 = m968;
	}

	public Double getM969() {
		return m969;
	}

	public void setM969(Double m969) {
		this.m969 = m969;
	}

	public Double getM970() {
		return m970;
	}

	public void setM970(Double m970) {
		this.m970 = m970;
	}

	public Double getM971() {
		return m971;
	}

	public void setM971(Double m971) {
		this.m971 = m971;
	}

	public Double getM972() {
		return m972;
	}

	public void setM972(Double m972) {
		this.m972 = m972;
	}

	public Double getM973() {
		return m973;
	}

	public void setM973(Double m973) {
		this.m973 = m973;
	}

	public Double getM974() {
		return m974;
	}

	public void setM974(Double m974) {
		this.m974 = m974;
	}

	public Double getM975() {
		return m975;
	}

	public void setM975(Double m975) {
		this.m975 = m975;
	}

	public Double getM976() {
		return m976;
	}

	public void setM976(Double m976) {
		this.m976 = m976;
	}

	public Double getM977() {
		return m977;
	}

	public void setM977(Double m977) {
		this.m977 = m977;
	}

	public Double getM978() {
		return m978;
	}

	public void setM978(Double m978) {
		this.m978 = m978;
	}

	public Double getM979() {
		return m979;
	}

	public void setM979(Double m979) {
		this.m979 = m979;
	}

	public Double getM980() {
		return m980;
	}

	public void setM980(Double m980) {
		this.m980 = m980;
	}

	public Double getM981() {
		return m981;
	}

	public void setM981(Double m981) {
		this.m981 = m981;
	}

	public Double getM982() {
		return m982;
	}

	public void setM982(Double m982) {
		this.m982 = m982;
	}

	public Double getM983() {
		return m983;
	}

	public void setM983(Double m983) {
		this.m983 = m983;
	}

	public Double getM984() {
		return m984;
	}

	public void setM984(Double m984) {
		this.m984 = m984;
	}

	public Double getM985() {
		return m985;
	}

	public void setM985(Double m985) {
		this.m985 = m985;
	}

	public Double getM986() {
		return m986;
	}

	public void setM986(Double m986) {
		this.m986 = m986;
	}

	public Double getM987() {
		return m987;
	}

	public void setM987(Double m987) {
		this.m987 = m987;
	}

	public Double getM988() {
		return m988;
	}

	public void setM988(Double m988) {
		this.m988 = m988;
	}

	public Double getM989() {
		return m989;
	}

	public void setM989(Double m989) {
		this.m989 = m989;
	}

	public Double getM990() {
		return m990;
	}

	public void setM990(Double m990) {
		this.m990 = m990;
	}

	public Double getM991() {
		return m991;
	}

	public void setM991(Double m991) {
		this.m991 = m991;
	}

	public Double getM992() {
		return m992;
	}

	public void setM992(Double m992) {
		this.m992 = m992;
	}

	public Double getM993() {
		return m993;
	}

	public void setM993(Double m993) {
		this.m993 = m993;
	}

	public Double getM994() {
		return m994;
	}

	public void setM994(Double m994) {
		this.m994 = m994;
	}

	public Double getM995() {
		return m995;
	}

	public void setM995(Double m995) {
		this.m995 = m995;
	}

	public Double getM996() {
		return m996;
	}

	public void setM996(Double m996) {
		this.m996 = m996;
	}

	public Double getM997() {
		return m997;
	}

	public void setM997(Double m997) {
		this.m997 = m997;
	}

	public Double getM998() {
		return m998;
	}

	public void setM998(Double m998) {
		this.m998 = m998;
	}

	public Double getM999() {
		return m999;
	}

	public void setM999(Double m999) {
		this.m999 = m999;
	}

	public Double getM1000() {
		return m1000;
	}

	public void setM1000(Double m1000) {
		this.m1000 = m1000;
	}

	public Double getM1001() {
		return m1001;
	}

	public void setM1001(Double m1001) {
		this.m1001 = m1001;
	}

	public Double getM1002() {
		return m1002;
	}

	public void setM1002(Double m1002) {
		this.m1002 = m1002;
	}

	public Double getM1003() {
		return m1003;
	}

	public void setM1003(Double m1003) {
		this.m1003 = m1003;
	}

	public Double getM1004() {
		return m1004;
	}

	public void setM1004(Double m1004) {
		this.m1004 = m1004;
	}

	public Double getM1005() {
		return m1005;
	}

	public void setM1005(Double m1005) {
		this.m1005 = m1005;
	}

	public Double getM1006() {
		return m1006;
	}

	public void setM1006(Double m1006) {
		this.m1006 = m1006;
	}

	public Double getM1007() {
		return m1007;
	}

	public void setM1007(Double m1007) {
		this.m1007 = m1007;
	}

	public Double getM1008() {
		return m1008;
	}

	public void setM1008(Double m1008) {
		this.m1008 = m1008;
	}

	public Double getM1009() {
		return m1009;
	}

	public void setM1009(Double m1009) {
		this.m1009 = m1009;
	}

	public Double getM1010() {
		return m1010;
	}

	public void setM1010(Double m1010) {
		this.m1010 = m1010;
	}

	public Double getM1011() {
		return m1011;
	}

	public void setM1011(Double m1011) {
		this.m1011 = m1011;
	}

	public Double getM1012() {
		return m1012;
	}

	public void setM1012(Double m1012) {
		this.m1012 = m1012;
	}

	public Double getM1013() {
		return m1013;
	}

	public void setM1013(Double m1013) {
		this.m1013 = m1013;
	}

	public Double getM1014() {
		return m1014;
	}

	public void setM1014(Double m1014) {
		this.m1014 = m1014;
	}

	public Double getM1015() {
		return m1015;
	}

	public void setM1015(Double m1015) {
		this.m1015 = m1015;
	}

	public Double getM1016() {
		return m1016;
	}

	public void setM1016(Double m1016) {
		this.m1016 = m1016;
	}

	public Double getM1017() {
		return m1017;
	}

	public void setM1017(Double m1017) {
		this.m1017 = m1017;
	}

	public Double getM1018() {
		return m1018;
	}

	public void setM1018(Double m1018) {
		this.m1018 = m1018;
	}

	public Double getM1019() {
		return m1019;
	}

	public void setM1019(Double m1019) {
		this.m1019 = m1019;
	}

	public Double getM1020() {
		return m1020;
	}

	public void setM1020(Double m1020) {
		this.m1020 = m1020;
	}

	public Double getM1021() {
		return m1021;
	}

	public void setM1021(Double m1021) {
		this.m1021 = m1021;
	}

	public Double getM1022() {
		return m1022;
	}

	public void setM1022(Double m1022) {
		this.m1022 = m1022;
	}

	public Double getM1023() {
		return m1023;
	}

	public void setM1023(Double m1023) {
		this.m1023 = m1023;
	}

	public Double getM1024() {
		return m1024;
	}

	public void setM1024(Double m1024) {
		this.m1024 = m1024;
	}

	public Double getM1025() {
		return m1025;
	}

	public void setM1025(Double m1025) {
		this.m1025 = m1025;
	}

	public Double getM1026() {
		return m1026;
	}

	public void setM1026(Double m1026) {
		this.m1026 = m1026;
	}

	public Double getM1027() {
		return m1027;
	}

	public void setM1027(Double m1027) {
		this.m1027 = m1027;
	}

	public Double getM1028() {
		return m1028;
	}

	public void setM1028(Double m1028) {
		this.m1028 = m1028;
	}

	public Double getM1029() {
		return m1029;
	}

	public void setM1029(Double m1029) {
		this.m1029 = m1029;
	}

	public Double getM1030() {
		return m1030;
	}

	public void setM1030(Double m1030) {
		this.m1030 = m1030;
	}

	public Double getM1031() {
		return m1031;
	}

	public void setM1031(Double m1031) {
		this.m1031 = m1031;
	}

	public Double getM1032() {
		return m1032;
	}

	public void setM1032(Double m1032) {
		this.m1032 = m1032;
	}

	public Double getM1033() {
		return m1033;
	}

	public void setM1033(Double m1033) {
		this.m1033 = m1033;
	}

	public Double getM1034() {
		return m1034;
	}

	public void setM1034(Double m1034) {
		this.m1034 = m1034;
	}

	public Double getM1035() {
		return m1035;
	}

	public void setM1035(Double m1035) {
		this.m1035 = m1035;
	}

	public Double getM1036() {
		return m1036;
	}

	public void setM1036(Double m1036) {
		this.m1036 = m1036;
	}

	public Double getM1037() {
		return m1037;
	}

	public void setM1037(Double m1037) {
		this.m1037 = m1037;
	}

	public Double getM1038() {
		return m1038;
	}

	public void setM1038(Double m1038) {
		this.m1038 = m1038;
	}

	public Double getM1039() {
		return m1039;
	}

	public void setM1039(Double m1039) {
		this.m1039 = m1039;
	}

	public Double getM1040() {
		return m1040;
	}

	public void setM1040(Double m1040) {
		this.m1040 = m1040;
	}

	public Double getM1041() {
		return m1041;
	}

	public void setM1041(Double m1041) {
		this.m1041 = m1041;
	}

	public Double getM1042() {
		return m1042;
	}

	public void setM1042(Double m1042) {
		this.m1042 = m1042;
	}

	public Double getM1043() {
		return m1043;
	}

	public void setM1043(Double m1043) {
		this.m1043 = m1043;
	}

	public Double getM1044() {
		return m1044;
	}

	public void setM1044(Double m1044) {
		this.m1044 = m1044;
	}

	public Double getM1045() {
		return m1045;
	}

	public void setM1045(Double m1045) {
		this.m1045 = m1045;
	}

	public Double getM1046() {
		return m1046;
	}

	public void setM1046(Double m1046) {
		this.m1046 = m1046;
	}

	public Double getM1047() {
		return m1047;
	}

	public void setM1047(Double m1047) {
		this.m1047 = m1047;
	}

	public Double getM1048() {
		return m1048;
	}

	public void setM1048(Double m1048) {
		this.m1048 = m1048;
	}

	public Double getM1049() {
		return m1049;
	}

	public void setM1049(Double m1049) {
		this.m1049 = m1049;
	}

	public Double getM1050() {
		return m1050;
	}

	public void setM1050(Double m1050) {
		this.m1050 = m1050;
	}

	public Double getM1051() {
		return m1051;
	}

	public void setM1051(Double m1051) {
		this.m1051 = m1051;
	}

	public Double getM1052() {
		return m1052;
	}

	public void setM1052(Double m1052) {
		this.m1052 = m1052;
	}

	public Double getM1053() {
		return m1053;
	}

	public void setM1053(Double m1053) {
		this.m1053 = m1053;
	}

	public Double getM1054() {
		return m1054;
	}

	public void setM1054(Double m1054) {
		this.m1054 = m1054;
	}

	public Double getM1055() {
		return m1055;
	}

	public void setM1055(Double m1055) {
		this.m1055 = m1055;
	}

	public Double getM1056() {
		return m1056;
	}

	public void setM1056(Double m1056) {
		this.m1056 = m1056;
	}

	public Double getM1057() {
		return m1057;
	}

	public void setM1057(Double m1057) {
		this.m1057 = m1057;
	}

	public Double getM1058() {
		return m1058;
	}

	public void setM1058(Double m1058) {
		this.m1058 = m1058;
	}

	public Double getM1059() {
		return m1059;
	}

	public void setM1059(Double m1059) {
		this.m1059 = m1059;
	}

	public Double getM1060() {
		return m1060;
	}

	public void setM1060(Double m1060) {
		this.m1060 = m1060;
	}

	public Double getM1061() {
		return m1061;
	}

	public void setM1061(Double m1061) {
		this.m1061 = m1061;
	}

	public Double getM1062() {
		return m1062;
	}

	public void setM1062(Double m1062) {
		this.m1062 = m1062;
	}

	public Double getM1063() {
		return m1063;
	}

	public void setM1063(Double m1063) {
		this.m1063 = m1063;
	}

	public Double getM1064() {
		return m1064;
	}

	public void setM1064(Double m1064) {
		this.m1064 = m1064;
	}

	public Double getM1065() {
		return m1065;
	}

	public void setM1065(Double m1065) {
		this.m1065 = m1065;
	}

	public Double getM1066() {
		return m1066;
	}

	public void setM1066(Double m1066) {
		this.m1066 = m1066;
	}

	public Double getM1067() {
		return m1067;
	}

	public void setM1067(Double m1067) {
		this.m1067 = m1067;
	}

	public Double getM1068() {
		return m1068;
	}

	public void setM1068(Double m1068) {
		this.m1068 = m1068;
	}

	public Double getM1069() {
		return m1069;
	}

	public void setM1069(Double m1069) {
		this.m1069 = m1069;
	}

	public Double getM1070() {
		return m1070;
	}

	public void setM1070(Double m1070) {
		this.m1070 = m1070;
	}

	public Double getM1071() {
		return m1071;
	}

	public void setM1071(Double m1071) {
		this.m1071 = m1071;
	}

	public Double getM1072() {
		return m1072;
	}

	public void setM1072(Double m1072) {
		this.m1072 = m1072;
	}

	public Double getM1073() {
		return m1073;
	}

	public void setM1073(Double m1073) {
		this.m1073 = m1073;
	}

	public Double getM1074() {
		return m1074;
	}

	public void setM1074(Double m1074) {
		this.m1074 = m1074;
	}

	public Double getM1075() {
		return m1075;
	}

	public void setM1075(Double m1075) {
		this.m1075 = m1075;
	}

	public Double getM1076() {
		return m1076;
	}

	public void setM1076(Double m1076) {
		this.m1076 = m1076;
	}

	public Double getM1077() {
		return m1077;
	}

	public void setM1077(Double m1077) {
		this.m1077 = m1077;
	}

	public Double getM1078() {
		return m1078;
	}

	public void setM1078(Double m1078) {
		this.m1078 = m1078;
	}

	public Double getM1079() {
		return m1079;
	}

	public void setM1079(Double m1079) {
		this.m1079 = m1079;
	}

	public Double getM1080() {
		return m1080;
	}

	public void setM1080(Double m1080) {
		this.m1080 = m1080;
	}

	public Double getM1081() {
		return m1081;
	}

	public void setM1081(Double m1081) {
		this.m1081 = m1081;
	}

	public Double getM1082() {
		return m1082;
	}

	public void setM1082(Double m1082) {
		this.m1082 = m1082;
	}

	public Double getM1083() {
		return m1083;
	}

	public void setM1083(Double m1083) {
		this.m1083 = m1083;
	}

	public Double getM1084() {
		return m1084;
	}

	public void setM1084(Double m1084) {
		this.m1084 = m1084;
	}

	public Double getM1085() {
		return m1085;
	}

	public void setM1085(Double m1085) {
		this.m1085 = m1085;
	}

	public Double getM1086() {
		return m1086;
	}

	public void setM1086(Double m1086) {
		this.m1086 = m1086;
	}

	public Double getM1087() {
		return m1087;
	}

	public void setM1087(Double m1087) {
		this.m1087 = m1087;
	}

	public Double getM1088() {
		return m1088;
	}

	public void setM1088(Double m1088) {
		this.m1088 = m1088;
	}

	public Double getM1089() {
		return m1089;
	}

	public void setM1089(Double m1089) {
		this.m1089 = m1089;
	}

	public Double getM1090() {
		return m1090;
	}

	public void setM1090(Double m1090) {
		this.m1090 = m1090;
	}

	public Double getM1091() {
		return m1091;
	}

	public void setM1091(Double m1091) {
		this.m1091 = m1091;
	}

	public Double getM1092() {
		return m1092;
	}

	public void setM1092(Double m1092) {
		this.m1092 = m1092;
	}

	public Double getM1093() {
		return m1093;
	}

	public void setM1093(Double m1093) {
		this.m1093 = m1093;
	}

	public Double getM1094() {
		return m1094;
	}

	public void setM1094(Double m1094) {
		this.m1094 = m1094;
	}

	public Double getM1095() {
		return m1095;
	}

	public void setM1095(Double m1095) {
		this.m1095 = m1095;
	}

	public Double getM1096() {
		return m1096;
	}

	public void setM1096(Double m1096) {
		this.m1096 = m1096;
	}

	public Double getM1097() {
		return m1097;
	}

	public void setM1097(Double m1097) {
		this.m1097 = m1097;
	}

	public Double getM1098() {
		return m1098;
	}

	public void setM1098(Double m1098) {
		this.m1098 = m1098;
	}

	public Double getM1099() {
		return m1099;
	}

	public void setM1099(Double m1099) {
		this.m1099 = m1099;
	}

	public Double getM1100() {
		return m1100;
	}

	public void setM1100(Double m1100) {
		this.m1100 = m1100;
	}

	public Double getM1101() {
		return m1101;
	}

	public void setM1101(Double m1101) {
		this.m1101 = m1101;
	}

	public Double getM1102() {
		return m1102;
	}

	public void setM1102(Double m1102) {
		this.m1102 = m1102;
	}

	public Double getM1103() {
		return m1103;
	}

	public void setM1103(Double m1103) {
		this.m1103 = m1103;
	}

	public Double getM1104() {
		return m1104;
	}

	public void setM1104(Double m1104) {
		this.m1104 = m1104;
	}

	public Double getM1105() {
		return m1105;
	}

	public void setM1105(Double m1105) {
		this.m1105 = m1105;
	}

	public Double getM1106() {
		return m1106;
	}

	public void setM1106(Double m1106) {
		this.m1106 = m1106;
	}

	public Double getM1107() {
		return m1107;
	}

	public void setM1107(Double m1107) {
		this.m1107 = m1107;
	}

	public Double getM1108() {
		return m1108;
	}

	public void setM1108(Double m1108) {
		this.m1108 = m1108;
	}

	public Double getM1109() {
		return m1109;
	}

	public void setM1109(Double m1109) {
		this.m1109 = m1109;
	}

	public Double getM1110() {
		return m1110;
	}

	public void setM1110(Double m1110) {
		this.m1110 = m1110;
	}

	public Double getM1111() {
		return m1111;
	}

	public void setM1111(Double m1111) {
		this.m1111 = m1111;
	}

	public Double getM1112() {
		return m1112;
	}

	public void setM1112(Double m1112) {
		this.m1112 = m1112;
	}

	public Double getM1113() {
		return m1113;
	}

	public void setM1113(Double m1113) {
		this.m1113 = m1113;
	}

	public Double getM1114() {
		return m1114;
	}

	public void setM1114(Double m1114) {
		this.m1114 = m1114;
	}

	public Double getM1115() {
		return m1115;
	}

	public void setM1115(Double m1115) {
		this.m1115 = m1115;
	}

	public Double getM1116() {
		return m1116;
	}

	public void setM1116(Double m1116) {
		this.m1116 = m1116;
	}

	public Double getM1117() {
		return m1117;
	}

	public void setM1117(Double m1117) {
		this.m1117 = m1117;
	}

	public Double getM1118() {
		return m1118;
	}

	public void setM1118(Double m1118) {
		this.m1118 = m1118;
	}

	public Double getM1119() {
		return m1119;
	}

	public void setM1119(Double m1119) {
		this.m1119 = m1119;
	}

	public Double getM1120() {
		return m1120;
	}

	public void setM1120(Double m1120) {
		this.m1120 = m1120;
	}

	public Double getM1121() {
		return m1121;
	}

	public void setM1121(Double m1121) {
		this.m1121 = m1121;
	}

	public Double getM1122() {
		return m1122;
	}

	public void setM1122(Double m1122) {
		this.m1122 = m1122;
	}

	public Double getM1123() {
		return m1123;
	}

	public void setM1123(Double m1123) {
		this.m1123 = m1123;
	}

	public Double getM1124() {
		return m1124;
	}

	public void setM1124(Double m1124) {
		this.m1124 = m1124;
	}

	public Double getM1125() {
		return m1125;
	}

	public void setM1125(Double m1125) {
		this.m1125 = m1125;
	}

	public Double getM1126() {
		return m1126;
	}

	public void setM1126(Double m1126) {
		this.m1126 = m1126;
	}

	public Double getM1127() {
		return m1127;
	}

	public void setM1127(Double m1127) {
		this.m1127 = m1127;
	}

	public Double getM1128() {
		return m1128;
	}

	public void setM1128(Double m1128) {
		this.m1128 = m1128;
	}

	public Double getM1129() {
		return m1129;
	}

	public void setM1129(Double m1129) {
		this.m1129 = m1129;
	}

	public Double getM1130() {
		return m1130;
	}

	public void setM1130(Double m1130) {
		this.m1130 = m1130;
	}

	public Double getM1131() {
		return m1131;
	}

	public void setM1131(Double m1131) {
		this.m1131 = m1131;
	}

	public Double getM1132() {
		return m1132;
	}

	public void setM1132(Double m1132) {
		this.m1132 = m1132;
	}

	public Double getM1133() {
		return m1133;
	}

	public void setM1133(Double m1133) {
		this.m1133 = m1133;
	}

	public Double getM1134() {
		return m1134;
	}

	public void setM1134(Double m1134) {
		this.m1134 = m1134;
	}

	public Double getM1135() {
		return m1135;
	}

	public void setM1135(Double m1135) {
		this.m1135 = m1135;
	}

	public Double getM1136() {
		return m1136;
	}

	public void setM1136(Double m1136) {
		this.m1136 = m1136;
	}

	public Double getM1137() {
		return m1137;
	}

	public void setM1137(Double m1137) {
		this.m1137 = m1137;
	}

	public Double getM1138() {
		return m1138;
	}

	public void setM1138(Double m1138) {
		this.m1138 = m1138;
	}

	public Double getM1139() {
		return m1139;
	}

	public void setM1139(Double m1139) {
		this.m1139 = m1139;
	}

	public Double getM1140() {
		return m1140;
	}

	public void setM1140(Double m1140) {
		this.m1140 = m1140;
	}

	public Double getM1141() {
		return m1141;
	}

	public void setM1141(Double m1141) {
		this.m1141 = m1141;
	}

	public Double getM1142() {
		return m1142;
	}

	public void setM1142(Double m1142) {
		this.m1142 = m1142;
	}

	public Double getM1143() {
		return m1143;
	}

	public void setM1143(Double m1143) {
		this.m1143 = m1143;
	}

	public Double getM1144() {
		return m1144;
	}

	public void setM1144(Double m1144) {
		this.m1144 = m1144;
	}

	public Double getM1145() {
		return m1145;
	}

	public void setM1145(Double m1145) {
		this.m1145 = m1145;
	}

	public Double getM1146() {
		return m1146;
	}

	public void setM1146(Double m1146) {
		this.m1146 = m1146;
	}

	public Double getM1147() {
		return m1147;
	}

	public void setM1147(Double m1147) {
		this.m1147 = m1147;
	}

	public Double getM1148() {
		return m1148;
	}

	public void setM1148(Double m1148) {
		this.m1148 = m1148;
	}

	public Double getM1149() {
		return m1149;
	}

	public void setM1149(Double m1149) {
		this.m1149 = m1149;
	}

	public Double getM1150() {
		return m1150;
	}

	public void setM1150(Double m1150) {
		this.m1150 = m1150;
	}

	public Double getM1151() {
		return m1151;
	}

	public void setM1151(Double m1151) {
		this.m1151 = m1151;
	}

	public Double getM1152() {
		return m1152;
	}

	public void setM1152(Double m1152) {
		this.m1152 = m1152;
	}

	public Double getM1153() {
		return m1153;
	}

	public void setM1153(Double m1153) {
		this.m1153 = m1153;
	}

	public Double getM1154() {
		return m1154;
	}

	public void setM1154(Double m1154) {
		this.m1154 = m1154;
	}

	public Double getM1155() {
		return m1155;
	}

	public void setM1155(Double m1155) {
		this.m1155 = m1155;
	}

	public Double getM1156() {
		return m1156;
	}

	public void setM1156(Double m1156) {
		this.m1156 = m1156;
	}

	public Double getM1157() {
		return m1157;
	}

	public void setM1157(Double m1157) {
		this.m1157 = m1157;
	}

	public Double getM1158() {
		return m1158;
	}

	public void setM1158(Double m1158) {
		this.m1158 = m1158;
	}

	public Double getM1159() {
		return m1159;
	}

	public void setM1159(Double m1159) {
		this.m1159 = m1159;
	}

	public Double getM1160() {
		return m1160;
	}

	public void setM1160(Double m1160) {
		this.m1160 = m1160;
	}

	public Double getM1161() {
		return m1161;
	}

	public void setM1161(Double m1161) {
		this.m1161 = m1161;
	}

	public Double getM1162() {
		return m1162;
	}

	public void setM1162(Double m1162) {
		this.m1162 = m1162;
	}

	public Double getM1163() {
		return m1163;
	}

	public void setM1163(Double m1163) {
		this.m1163 = m1163;
	}

	public Double getM1164() {
		return m1164;
	}

	public void setM1164(Double m1164) {
		this.m1164 = m1164;
	}

	public Double getM1165() {
		return m1165;
	}

	public void setM1165(Double m1165) {
		this.m1165 = m1165;
	}

	public Double getM1166() {
		return m1166;
	}

	public void setM1166(Double m1166) {
		this.m1166 = m1166;
	}

	public Double getM1167() {
		return m1167;
	}

	public void setM1167(Double m1167) {
		this.m1167 = m1167;
	}

	public Double getM1168() {
		return m1168;
	}

	public void setM1168(Double m1168) {
		this.m1168 = m1168;
	}

	public Double getM1169() {
		return m1169;
	}

	public void setM1169(Double m1169) {
		this.m1169 = m1169;
	}

	public Double getM1170() {
		return m1170;
	}

	public void setM1170(Double m1170) {
		this.m1170 = m1170;
	}

	public Double getM1171() {
		return m1171;
	}

	public void setM1171(Double m1171) {
		this.m1171 = m1171;
	}

	public Double getM1172() {
		return m1172;
	}

	public void setM1172(Double m1172) {
		this.m1172 = m1172;
	}

	public Double getM1173() {
		return m1173;
	}

	public void setM1173(Double m1173) {
		this.m1173 = m1173;
	}

	public Double getM1174() {
		return m1174;
	}

	public void setM1174(Double m1174) {
		this.m1174 = m1174;
	}

	public Double getM1175() {
		return m1175;
	}

	public void setM1175(Double m1175) {
		this.m1175 = m1175;
	}

	public Double getM1176() {
		return m1176;
	}

	public void setM1176(Double m1176) {
		this.m1176 = m1176;
	}

	public Double getM1177() {
		return m1177;
	}

	public void setM1177(Double m1177) {
		this.m1177 = m1177;
	}

	public Double getM1178() {
		return m1178;
	}

	public void setM1178(Double m1178) {
		this.m1178 = m1178;
	}

	public Double getM1179() {
		return m1179;
	}

	public void setM1179(Double m1179) {
		this.m1179 = m1179;
	}

	public Double getM1180() {
		return m1180;
	}

	public void setM1180(Double m1180) {
		this.m1180 = m1180;
	}

	public Double getM1181() {
		return m1181;
	}

	public void setM1181(Double m1181) {
		this.m1181 = m1181;
	}

	public Double getM1182() {
		return m1182;
	}

	public void setM1182(Double m1182) {
		this.m1182 = m1182;
	}

	public Double getM1183() {
		return m1183;
	}

	public void setM1183(Double m1183) {
		this.m1183 = m1183;
	}

	public Double getM1184() {
		return m1184;
	}

	public void setM1184(Double m1184) {
		this.m1184 = m1184;
	}

	public Double getM1185() {
		return m1185;
	}

	public void setM1185(Double m1185) {
		this.m1185 = m1185;
	}

	public Double getM1186() {
		return m1186;
	}

	public void setM1186(Double m1186) {
		this.m1186 = m1186;
	}

	public Double getM1187() {
		return m1187;
	}

	public void setM1187(Double m1187) {
		this.m1187 = m1187;
	}

	public Double getM1188() {
		return m1188;
	}

	public void setM1188(Double m1188) {
		this.m1188 = m1188;
	}

	public Double getM1189() {
		return m1189;
	}

	public void setM1189(Double m1189) {
		this.m1189 = m1189;
	}

	public Double getM1190() {
		return m1190;
	}

	public void setM1190(Double m1190) {
		this.m1190 = m1190;
	}

	public Double getM1191() {
		return m1191;
	}

	public void setM1191(Double m1191) {
		this.m1191 = m1191;
	}

	public Double getM1192() {
		return m1192;
	}

	public void setM1192(Double m1192) {
		this.m1192 = m1192;
	}

	public Double getM1193() {
		return m1193;
	}

	public void setM1193(Double m1193) {
		this.m1193 = m1193;
	}

	public Double getM1194() {
		return m1194;
	}

	public void setM1194(Double m1194) {
		this.m1194 = m1194;
	}

	public Double getM1195() {
		return m1195;
	}

	public void setM1195(Double m1195) {
		this.m1195 = m1195;
	}

	public Double getM1196() {
		return m1196;
	}

	public void setM1196(Double m1196) {
		this.m1196 = m1196;
	}

	public Double getM1197() {
		return m1197;
	}

	public void setM1197(Double m1197) {
		this.m1197 = m1197;
	}

	public Double getM1198() {
		return m1198;
	}

	public void setM1198(Double m1198) {
		this.m1198 = m1198;
	}

	public Double getM1199() {
		return m1199;
	}

	public void setM1199(Double m1199) {
		this.m1199 = m1199;
	}

	public Double getM1200() {
		return m1200;
	}

	public void setM1200(Double m1200) {
		this.m1200 = m1200;
	}
	
		
}



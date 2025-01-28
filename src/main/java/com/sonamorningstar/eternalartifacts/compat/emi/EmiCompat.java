package com.sonamorningstar.eternalartifacts.compat.emi;

import com.sonamorningstar.eternalartifacts.compat.ModHooks;

public class EmiCompat {
	
	public static void runData() {
		addLang("en_us", "emi.category.eternalartifacts.fake_recipe.meat_packer", "Meat Packer");
		addLang("en_us", "emi.category.eternalartifacts.meat_shredding", "Meat Shredder");
		addLang("en_us", "emi.category.eternalartifacts.mob_liquifying", "Mob Liquifier");
		addLang("en_us", "emi.category.eternalartifacts.macerating", "Macerating");
		addLang("en_us", "emi.category.eternalartifacts.alloying", "Alloying");
		addLang("en_us", "emi.category.eternalartifacts.compressing", "Compressing");
		addLang("en_us", "emi.category.eternalartifacts.fluid_infusing", "Fluid Infusing");
		addLang("en_us", "emi.category.eternalartifacts.squeezing", "Squeezing");
		addLang("en_us", "emi.category.eternalartifacts.melting", "Melting");
		addLang("en_us", "emi.category.eternalartifacts.solidifying", "Solidifying");
		addLang("en_us", "emi.category.eternalartifacts.fake_recipe.hammering", "Hammering");
		
		addLang("tr_tr", "emi.category.eternalartifacts.fake_recipe.meat_packer", "Et Paketleyici");
		addLang("tr_tr", "emi.category.eternalartifacts.meat_shredding", "Et Parçalayıcı");
		addLang("tr_tr", "emi.category.eternalartifacts.mob_liquifying", "Canavar Sıvılaştırıcı");
		addLang("tr_tr", "emi.category.eternalartifacts.macerating", "Öğütme");
		addLang("tr_tr", "emi.category.eternalartifacts.alloying", "Alaşım");
		addLang("tr_tr", "emi.category.eternalartifacts.compressing", "Sıkıştırma");
		addLang("tr_tr", "emi.category.eternalartifacts.fluid_infusing", "Sıvı Enfüzyon");
		addLang("tr_tr", "emi.category.eternalartifacts.squeezing", "Sıkma");
		addLang("tr_tr", "emi.category.eternalartifacts.melting", "Eritme");
		addLang("tr_tr", "emi.category.eternalartifacts.solidifying", "Katılaştırma");
		addLang("tr_tr", "emi.category.eternalartifacts.fake_recipe.hammering", "Çekiçleme");
	}
	
	private static void addLang(String loc, String key, String value) {
		ModHooks.LanguageProvider.appendLang(loc, key, value);
	}
}

const getProductImageByCategory = (category) => {
  const normalized = String(category || "").toLowerCase();

  const imageMap = [
    { keywords: ["mobile", "phone", "smartphone", "cell"], url: "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["book", "novel", "literature", "story", "comic"], url: "https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["laptop", "computer", "notebook", "pc", "mac"], url: "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["headphone", "earbud", "audio", "speaker", "music"], url: "https://images.unsplash.com/photo-1512314889357-e157c22f938d?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["fashion", "clothing", "apparel", "shoes", "accessory", "jacket", "dress", "shirt"], url: "https://images.unsplash.com/photo-1520975865503-2d3e0dfa522f?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["furniture", "sofa", "table", "chair", "couch", "home"], url: "https://images.unsplash.com/photo-1493666438817-866a91353ca9?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["grocery", "food", "fruit", "vegetable", "snack", "drink"], url: "https://images.unsplash.com/photo-1506806732259-39c2d0268443?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["beauty", "skincare", "makeup", "cosmetic", "perfume"], url: "https://images.unsplash.com/photo-1522337660859-02fbefca4702?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["sports", "fitness", "bike", "ball", "exercise", "outdoor"], url: "https://images.unsplash.com/photo-1521412644187-c49fa049e84d?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["toy", "game", "board", "kids", "child"], url: "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["pet", "dog", "cat", "animal"], url: "https://images.unsplash.com/photo-1507146426996-ef05306b995a?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["jewelry", "ring", "necklace"], url: "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=900&q=60" },
    { keywords: ["watch", "wrist", "timepiece"], url: "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=900&q=60" },
  ];

  const match = imageMap.find((entry) =>
    entry.keywords.some((keyword) => normalized.includes(keyword))
  );

  return (
    match?.url ||
    "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=900&q=60"
  );
};

export default getProductImageByCategory;

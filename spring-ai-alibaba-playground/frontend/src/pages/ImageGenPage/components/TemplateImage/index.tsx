import React from "react";
import { Button } from "antd";

interface TemplateImageProps {
  id: string;
  prompt: string;
  path: string;
  onUseCreative: (prompt: string) => void;
}

const TemplateImage: React.FC<TemplateImageProps> = ({
  id,
  prompt,
  path,
  onUseCreative,
}) => {
  return (
    <div
      key={`template-image-${id}`}
      style={{
        position: "relative",
        marginBottom: "16px",
        breakInside: "avoid",
        borderRadius: "8px",
        overflow: "hidden",
        background: "#fff",
        boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)",
        transition: "transform 0.2s ease",
      }}
      onMouseEnter={(e) => {
        const overlay = e.currentTarget.querySelector(
          ".overlay"
        ) as HTMLElement;
        if (overlay) {
          overlay.style.opacity = "1";
        }
        e.currentTarget.style.transform = "translateY(-4px)";
      }}
      onMouseLeave={(e) => {
        const overlay = e.currentTarget.querySelector(
          ".overlay"
        ) as HTMLElement;
        if (overlay) {
          overlay.style.opacity = "0";
        }
        e.currentTarget.style.transform = "translateY(0)";
      }}
    >
      <img
        src={`/genImages/${path}`}
        alt={prompt}
        style={{
          width: "100%",
          height: "auto",
          display: "block",
          objectFit: "cover",
        }}
      />
      <div
        className="overlay"
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background:
            "linear-gradient(to bottom, rgba(0,0,0,0.1), rgba(0,0,0,0.7))",
          display: "flex",
          flexDirection: "column",
          justifyContent: "flex-end",
          padding: "20px",
          opacity: 0,
          transition: "opacity 0.3s ease",
        }}
      >
        <p
          style={{
            color: "white",
            margin: "0 0 16px 0",
            fontSize: "13px",
            WebkitLineClamp: 3,
            WebkitBoxOrient: "vertical",
            overflow: "hidden",
            display: "-webkit-box",
          }}
        >
          {prompt}
        </p>
        <Button
          type="primary"
          onClick={() => onUseCreative(prompt)}
          style={{
            alignSelf: "flex-start",
            padding: "4px 16px",
            height: "32px",
            borderRadius: "4px",
            fontSize: "15px",
            background: "#1890ff",
            border: "none",
            fontWeight: 500,
          }}
        >
          使用创意
        </Button>
      </div>
    </div>
  );
};

export default TemplateImage;

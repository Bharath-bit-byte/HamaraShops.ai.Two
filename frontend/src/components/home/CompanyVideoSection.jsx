import React, { useState, useRef } from 'react';
import { motion } from 'framer-motion';
import { Sparkles, Play, Pause, Film } from 'lucide-react';

// =========================================================================
// TEMPORARY DEMO VIDEO CONFIGURATION
// To replace this demo video with the official HamaraShops.ai company video:
// 1. Update COMPANY_VIDEO_SRC to your video file path (e.g. '/videos/company_video.mp4')
// 2. Optionally update COMPANY_VIDEO_POSTER to your video thumbnail image path
// =========================================================================
export const COMPANY_VIDEO_SRC = 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4';
export const COMPANY_VIDEO_POSTER = '/images/about_ai.png';

export default function CompanyVideoSection() {
  const [isPlaying, setIsPlaying] = useState(false);
  const [hasError, setHasError] = useState(false);
  const videoRef = useRef(null);

  const handlePlay = async () => {
    if (!videoRef.current) return;
    try {
      videoRef.current.muted = true;
      await videoRef.current.play();
      setIsPlaying(true);
      setHasError(false);
    } catch (err) {
      console.warn('Autoplay failed or blocked by browser policy:', err);
      setIsPlaying(false);
    }
  };

  const handlePause = () => {
    if (!videoRef.current) return;
    videoRef.current.pause();
    setIsPlaying(false);
  };

  const togglePlayPause = () => {
    if (isPlaying) {
      handlePause();
    } else {
      handlePlay();
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === ' ' || e.key === 'Enter') {
      e.preventDefault();
      togglePlayPause();
    }
  };

  return (
    <section id="company-video-section" className="py-16 px-6 bg-[#0c0e12] border-b border-[#3c475a]/30 relative overflow-hidden">
      <div className="max-w-6xl mx-auto relative z-10">
        
        {/* 1. COMPANY TAGLINE */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: '-40px' }}
          transition={{ duration: 0.6 }}
          className="text-center max-w-4xl mx-auto mb-12"
        >
          <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-[#ff6b6b]/10 border border-[#ff6b6b]/30 text-[#ffb3b0] text-xs font-mono mb-4 uppercase tracking-widest">
            <Sparkles className="w-4 h-4 text-[#ff6b6b]" />
            <span>OUR POSITIONING</span>
          </div>

          <h2 className="font-headline text-2xl sm:text-3xl md:text-4xl font-extrabold text-white leading-tight tracking-tight">
            "HamaraShops.ai is an{' '}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-[#ff6b6b] via-[#ff8533] to-[#4cd6ff]">
              application player
            </span>{' '}
            in the AI race."
          </h2>
        </motion.div>

        {/* 2. LARGE CINEMATIC VIDEO CONTAINER */}
        <motion.div
          initial={{ opacity: 0, scale: 0.96, y: 30 }}
          whileInView={{ opacity: 1, scale: 1, y: 0 }}
          viewport={{ once: true, margin: '-40px' }}
          transition={{ duration: 0.6, delay: 0.15 }}
          className="relative w-full max-w-5xl mx-auto rounded-3xl overflow-hidden bg-[#0a1628]/80 border border-[#ff6b6b]/30 shadow-2xl shadow-[#ff6b6b]/10 group"
          onMouseEnter={handlePlay}
          onMouseLeave={handlePause}
        >
          {/* 16:9 Aspect Ratio Outer Frame */}
          <div 
            className="relative w-full aspect-video bg-black/90 flex items-center justify-center cursor-pointer select-none"
            onClick={togglePlayPause}
            onKeyDown={handleKeyDown}
            tabIndex={0}
            role="button"
            aria-label={isPlaying ? 'Pause Company Overview Video' : 'Play Company Overview Video'}
          >
            <video
              ref={videoRef}
              src={COMPANY_VIDEO_SRC}
              poster={COMPANY_VIDEO_POSTER}
              muted
              playsInline
              preload="none"
              onError={() => setHasError(true)}
              onEnded={() => setIsPlaying(false)}
              className="w-full h-full object-cover transition-opacity duration-300"
            />

            {/* Play/Pause Overlay Indicator */}
            <div 
              className={`absolute inset-0 bg-black/40 backdrop-blur-[2px] flex flex-col items-center justify-center transition-opacity duration-300 ${
                isPlaying ? 'opacity-0 hover:opacity-100' : 'opacity-100'
              }`}
            >
              <div className="w-16 h-16 sm:w-20 sm:h-20 rounded-full bg-gradient-to-br from-[#ff6b6b] to-[#ff8533] text-[#68000f] flex items-center justify-center shadow-xl shadow-[#ff6b6b]/40 transform group-hover:scale-110 transition-transform duration-300 mb-3">
                {isPlaying ? (
                  <Pause className="w-8 h-8 text-[#68000f] fill-[#68000f]" />
                ) : (
                  <Play className="w-8 h-8 text-[#68000f] fill-[#68000f] translate-x-0.5" />
                )}
              </div>

              <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-black/60 border border-white/10 text-white text-xs font-mono">
                <Film className="w-3.5 h-3.5 text-[#4cd6ff]" />
                <span>{isPlaying ? 'Pause Preview' : 'Hover / Tap to Play Video'}</span>
              </div>
            </div>

            {/* Error Message Fallback */}
            {hasError && (
              <div className="absolute inset-0 bg-[#0a1628]/95 flex items-center justify-center p-6 text-center">
                <p className="text-slate-300 text-sm font-mono">
                  Demo video preview unavailable. Replace <code className="text-[#ff6b6b]">COMPANY_VIDEO_SRC</code> with the final video URL.
                </p>
              </div>
            )}
          </div>

          {/* Bottom Bar Label */}
          <div className="p-4 bg-[#0a1628] border-t border-[#3c475a]/40 flex flex-wrap items-center justify-between gap-3 text-xs font-mono text-slate-400">
            <span className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-[#ff6b6b] animate-pulse" />
              <span className="text-white font-semibold">HamaraShops.ai Official Video</span>
              <span className="text-slate-500">(Demo Placeholder)</span>
            </span>
            <span className="text-[#4cd6ff]">Hover on Desktop • Tap on Mobile</span>
          </div>
        </motion.div>

      </div>
    </section>
  );
}
